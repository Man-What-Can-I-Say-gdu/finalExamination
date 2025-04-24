package com.Pumpkin.Controller;

import com.Pumpkin.Service.GameService;
import com.Pumpkin.Service.ServiceImp.GameServiceImp;
import com.Pumpkin.Service.ServiceImp.RoomServiceImp;
import com.Pumpkin.entity.Gamer;
import com.Pumpkin.entity.HistoricGame;
import com.Pumpkin.entity.OnlineOperator;
import com.Pumpkin.entity.Room;
import com.alibaba.fastjson2.JSON;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/Game")
public class middleServerEndpoint {
//    /**
//     * 存储房主的Id和session
//     */
//    private static Map<Integer, Session> roomOwner = new ConcurrentHashMap<Integer, Session>();
//    /**
//     * 存放房客的Id和session
//     */
//    private static Map<Integer,Session> roomGuest = new ConcurrentHashMap<Integer, Session>();
    /**
     * 存放操作方的数据
     */
    private OnlineOperator operator = new OnlineOperator();
    /**
     * 存放入房方或对手的信息
     */
    private OnlineOperator opponent = new OnlineOperator();
    /**
     * 存放客户端的Uri和session
     */
    private Map<String, Session> clients = new ConcurrentHashMap<String,Session>();

    /**
     * 存放游戏开始后对手的信息
     */
    /**
     * 用于调用房间操作
     */
    private static RoomServiceImp roomServiceImp = new RoomServiceImp();
    /**
     * 存放操作对象加入或创建的房间
     */
    private static Room room = new Room();
    /**
     * 存放对局数据，在游戏结束后对游戏数据进行保存
     */
    HistoricGame historicGame = new HistoricGame();
    GameService gameService = new GameServiceImp();
    @OnOpen
    public void OnOpen(Session session,String SessionURL){
        //sessionURL是唯一id，在建立连接后将数据添加到clients
        clients.put(SessionURL,session);
        operator.setSession(session);
        sendTextMessage(operator.getSession(),"connectSuccess");
        //启动一个定时器，发送ping消息到客户端
        Timer timer = new Timer();
        //每30秒发送一次ping消息
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                session.getAsyncRemote().sendText("ping!");
            }
        },0,30000);
        //1分钟为接收到响应则客户端离线，自动断开
        session.setMaxIdleTimeout(60000L);
    }

    @OnMessage
    public void OnText(String info) {
        //经过设计的消息，需要进行解析获得指令并执行相应的操作
        String[] infoPart = info.split("\\.", 2);
        if(infoPart.length == 2){
            try {
                //对room数据进行解析
                room = parseRoom(infoPart[1]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if("buildRoom".equals(infoPart[0])){
                buildRoom(room);
            }else if("joinRoom".equals(infoPart[0])){
                joinRoom(room.getGuestId(),room);
            }else if("exitRoom".equals(infoPart[0])){
                exitRoom(room);
            }else if("updateRoom".equals(infoPart[0])){
                updateRoom(room);
            }else if("chessing".equals(infoPart[0])){
                //将棋子数据传递给对方
                sendTextMessage(opponent.getSession(),info);
            }else if("gamerType".equals(infoPart[0])){
                //开始游戏后随机决定正反方，并将数据传入服务器，服务器记录正反方数据
                if(infoPart[1].equals("white")){
                    operator.setGameIdentity(OnlineOperator.gameIdentity.White);
                    opponent.setGameIdentity(OnlineOperator.gameIdentity.Black);
                }else{
                    operator.setGameIdentity(OnlineOperator.gameIdentity.Black);
                    opponent.setGameIdentity(OnlineOperator.gameIdentity.White);
                }
            }
        }else if(infoPart.length == 1){
            //消息指令
            switch (info){
                case "withdraw":{
                    //悔棋
                    sendTextMessage(opponent.getSession(),"opponentWithdraw");
                    break;
                } case "surrender":{
                    //认输
                    sendTextMessage(opponent.getSession(),"opponentSurrender");
                    break;
                } case "lost":{
                    //失败
                    sendTextMessage(opponent.getSession(),"opponentLost");
                    break;
                } case "victory":{
                    sendTextMessage(opponent.getSession(),"opponentVictory");
                    historicGame.setWinnerType(!OnlineOperator.gameIdentity.Black.equals(operator.getGameIdentity()));
                    gameService.saveGame(historicGame);
                    break;
                } case "start" :{
                    //start操作由房主发出
                    String GameId = GameController.startGame();
                    sendTextMessage(operator.getSession(),"startGame"+GameId);
                    sendTextMessage(opponent.getSession(), "startGame"+GameId);
                    historicGame.setGameId(GameId);
                    historicGame.setStartTime(GameId.substring(0,13));
                    Random random = new Random();
                }

            }
        }
    }
    private static Room parseRoom(String roomJson) throws IOException {
        return JSON.parseObject(roomJson,Room.class);
    }

    @OnMessage
    public void onBinary(byte[] info){
        //传入的message也为两部分组成，但是需要对二进制数据解析后才能获得，格式与字符串的一致
        String information = new String(info);
        if("guestResponseToJoin".equals(information.split("\\.")[0])){
            //房客客户端响应加入房间的请求，此时携带的数据为用户的数据，直接将数据传递给房主即可
            sendBytesMessage(opponent.getSession(),info);
        }else if("ownerResponseToJoin".equals(information.split("\\.")[0])){
            //房主客户端响应加入房间的请求，此时携带的数据为用户的数据，直接将数据传递给房客即可
            sendBytesMessage(opponent.getSession(), info);
        }
    }

    @OnMessage
    public void onPong(PongMessage message){
        //接收到Pong消息
    }

    /**
     * 处理创建房间请求，并将房主id和session关联，方便后续调用
     * @param room
     * @return
     */
    public boolean buildRoom(Room room) {
        if(roomServiceImp.buildRoom(room)){
            operator.setIdentity(OnlineOperator.roomIdentity.Owner);
            return true;
        }
        return false;
    }
    public void joinRoom(int guestId, Room room) {
        if(roomServiceImp.joinRoom(room,guestId)){
            operator.setIdentity(OnlineOperator.roomIdentity.Guest);
            //向数据库中添加用户数据成功，此时向房主客户端发送加房通知，并向房客发送加房成功通知
            room = roomServiceImp.selectRoom(room.getRoomId());
            //向房主发送加房通知，通知格式为Json字符串包含房客信息
            sendTextMessage(opponent.getSession(), "joinInNotice");
            //向房客发送加房成功通知
            sendTextMessage(operator.getSession(), "joinInSuccess");
            //发送完后等待客户端相响应数据
        }
    }

    /**
     * 实现更改房间信息功能
     * @param room
     */
    public void updateRoom(Room room) {
        if(roomServiceImp.updateRoom(room)){
            if(room.getGuestId() != 0){
                sendTextMessage(operator.getSession(), "roomInfo."+JSON.toJSONString(room));
            }
        }else{
            sendTextMessage(operator.getSession(), "updateRoomFalse");
        }
    }
    public void exitRoom(Room room) {
        if(operator.getIdentity().equals(OnlineOperator.roomIdentity.Owner)){
            //如果是房主的话
            if(room.getGuestId() != 0){
                //如果房间中有房客的话，将房客成为房主
                operator.setIdentity(OnlineOperator.roomIdentity.Owner);
                opponent.setIdentity(null);
                opponent.setSession(null);
                opponent.setOperatorId(0);
                opponent.setGameIdentity(null);
                //清除数据库的owner信息，将guest的信息转移到Owner上
                roomServiceImp.exchangeGuestAndOwner(room);
                roomServiceImp.deleteGuest(room);
                room = roomServiceImp.selectRoom(room.getRoomId());
                sendTextMessage(operator.getSession(), "OwnerExit");
            }else{
                //如果房间中没有房客的话，直接摧毁房间
                destoryRoom(room);
            }
        }else if(operator.getIdentity().equals(OnlineOperator.roomIdentity.Guest)){
            //如果是房客的话，清除房客信息
            if(roomServiceImp.deleteGuest(room)){
                opponent.setIdentity(null);
                opponent.setSession(null);
                opponent.setOperatorId(0);
                opponent.setGameIdentity(null);
                //向服务器发送房客退出的信息
                sendTextMessage(operator.getSession(),"GuestOutOfRoom");
            }

        }
    }
    public void destoryRoom(Room room) {
        //在数据库清空房间信息
        if(roomServiceImp.destoryRoom(room.getRoomId())){
            //将本地的Owner信息清空
            operator.setIdentity(null);
        }
    }


    private Gamer parseToGamer(String GamerJson){
        return JSON.parseObject(GamerJson,Gamer.class);
    }

    /**
     * 传输字符串数据
     * @param info
     */
    private void sendTextMessage(Session target,String info){
        try {
            target.getBasicRemote().sendText(info);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 传输二进制数据
     * @param info
     */
    private void sendBytesMessage(Session target,byte[] info){
        try {
            target.getBasicRemote().sendBinary(ByteBuffer.wrap(info));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session){

    }

    @OnError
    public void onError(Session session){

    }


}
