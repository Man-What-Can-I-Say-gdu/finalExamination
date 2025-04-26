package com.Pumpkin.Controller;

import com.Pumpkin.Dao.ChessBoardStyleDao;
import com.Pumpkin.Service.ChessBoardService;
import com.Pumpkin.Service.GameService;
import com.Pumpkin.Service.ServiceImp.ChessBoardServiceImp;
import com.Pumpkin.Service.ServiceImp.GameServiceImp;
import com.Pumpkin.Service.ServiceImp.RoomServiceImp;
import com.Pumpkin.entity.*;
import com.Pumpkin.entity.utils.ELOResult;
import com.Pumpkin.entity.utils.EloUtils;
import com.alibaba.fastjson2.JSON;
import javafx.scene.control.cell.CheckBoxListCell;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@ServerEndpoint("/Game")
public class middleServerEndpoint {

    /**
     * 存放多线程产生的房间
     * 第一个参数时sessionId
     */
    private static final ConcurrentHashMap<String, InternalRoom> rooms = new ConcurrentHashMap<>();


    /**
     * 线程池
     */
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * 线程安全的集合，用来存放session数据
     * 第一个数据为sessionId，第二个数据为session
     */
    private ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<>();


    /**
     * 用于调用房间操作
     */
    private static RoomServiceImp roomServiceImp = new RoomServiceImp();



    /**
     * 存放对局数据，在游戏结束后对游戏数据进行保存
     */
    ChessBoardService chessBoardService = new ChessBoardServiceImp();


    GameService gameService = new GameServiceImp();


    GameController gameController = new GameController();


    /**
     * 存放快速匹配的容器
     */
    CopyOnWriteArrayList<OnlineOperator> MatchVector = new CopyOnWriteArrayList<>();


    /**
     * 最大得分
     */
    public static final int maxPoints = 40;

    @OnOpen
    public void OnOpen(Session session){
        System.out.println("连接成功");
        //在建立连接后将数据添加到clients
        clients.put(session.getId(),session);
        sendTextMessage(clients.get(session.getId()),"connectSuccess");
        //启动一个定时器，发送ping消息到客户端
        Timer timer = new Timer();
        //每30秒发送一次ping消息
        timer.schedule(new TimerTask() {
            boolean isClientConnect = false;
            @Override
            public void run() {
                if(!isClientConnect){
                    //为假，向客户端发送对方以下线的提示
                    sendTextMessage(session,"opponentLostConnect");
                }
                isClientConnect = false;
            }
        },0,34000);
        //1分钟为接收到响应则客户端离线，自动断开
        session.setMaxIdleTimeout(60000L);
        sendTextMessage(session,"opponentReconnectFailed");
    }

    @OnMessage
    public void OnText(String info, Session session) {
        executor.execute(() ->handleTextInfo(info, session));
    }

    private void handleTextInfo(String info, Session operatorSession) {
        //经过设计的消息，需要进行解析获得指令并执行相应的操作
        String[] infoPart = info.split("\\.", 2);
        Room room = new Room();
        if(infoPart.length == 2){
            try {
                //对room数据进行解析
                room = parseRoom(infoPart[1]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if("buildRoom".equals(infoPart[0])){
                buildRoom(room,operatorSession);
            }else if("joinRoom".equals(infoPart[0])){
                joinRoom(room.getGuestId(),room,operatorSession);
            }else if("exitRoom".equals(infoPart[0])){
                exitRoom(room,operatorSession);
            }else if("updateRoom".equals(infoPart[0])){
                updateRoom(room,operatorSession);
            }else if("chessing".equals(infoPart[0])){
                //将棋子数据传递给对方
                sendTextMessage(rooms.get(operatorSession.getId()).getGuest().getSession(), info);
            } else if ("finalChessBoardStyle".equals(infoPart[0])) {
                HistoricGame historicGame = new HistoricGame();
                chessBoardService.buildChessBoard(historicGame.getGameId());
                //将存放到数组中的解析出来
                ArrayList<Chess> chess = (ArrayList<Chess>) JSON.parseArray(infoPart[1],Chess.class);
                //存储到数据库
                chessBoardService.printChessBoard(historicGame.getGameId(), chess);
            } else if ("quickMatch".equals(infoPart[0])){
                //快速匹配：根据ELO算法

                //携带game数据进行匹配

                Gamer gamer = JSON.parseObject(infoPart[1],Gamer.class);

                OnlineOperator operator = new OnlineOperator();
                operator.setSession(operatorSession);
                operator.setGamer(gamer);

                MatchVector.add(operator);
//                Timer timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        MatchVector.forEach((opponent)->{
//                            int operatorPoints = operator.getGamer().getPoints();
//                            int opponentPoints = opponent.getGamer().getPoints();
//                            ELOResult result = EloUtils.rating(operatorPoints,opponentPoints, operatorPoints>opponentPoints? 1 : 0, maxPoints, false);
//                            if(result.)
//                        });
//                    }
//                });


            }
        }else if(infoPart.length == 1){
            //消息指令
            switch (info){
                case "withdraw":{
                    //悔棋
                    sendTextMessage(rooms.get(operatorSession.getId()).getGuest().getSession(),"opponentWithdraw");
                    break;
                } case "surrender":{
                    //认输
                    sendTextMessage(rooms.get(operatorSession.getId()).getGuest().getSession(),"opponentSurrender");
                    break;
                } case "lost":{
                    //向前端发送操作用户失败的信息
                    sendTextMessage(rooms.get(operatorSession.getId()).getGuest().getSession(),"opponentLost");
                    break;
                } case "victory":{
                    HistoricGame historicGame = new HistoricGame();
                    //向对手发送操作用户胜利的信息，操作用户接收到后发送给后端，由后端接收并进行数据保存
                    sendTextMessage(rooms.get(operatorSession.getId()).getGuest().getSession(),"opponentVictory");
                    historicGame.setWinnerType(!OnlineOperator.gameIdentity.Black.equals(rooms.get(operatorSession.getId()).getGuest().getGameIdentity()));
                    gameService.saveGame(historicGame);
                    break;
                } case "start" :{
                    //start操作由房主发出


                    HistoricGame historicGame = new HistoricGame();

                    OnlineOperator operator = rooms.get(operatorSession.getId()).getOwner();
                    OnlineOperator opponent = rooms.get(operatorSession.getId()).getGuest();

                    //获取对局id
                    String GameId = GameController.startGame();


                    //向双方发送游戏开始信息,包含对局id
                    sendTextMessage(operatorSession,"startGame."+GameId);
                    sendTextMessage(rooms.get(operatorSession.getId()).getGuest().getSession(), "startGame."+GameId);


                    historicGame.setGameId(GameId);
                    historicGame.setStartTime(GameId.substring(0,13));
                    //开始游戏后随机决定正反方，并将数据传入服务器，服务器记录正反方数据
                    boolean operatorGameIdentity = gameController.getRandomGamerType();
                    operator.setGameIdentity(operatorGameIdentity? OnlineOperator.gameIdentity.Black : OnlineOperator.gameIdentity.White);
                    opponent.setGameIdentity(operatorGameIdentity? OnlineOperator.gameIdentity.White : OnlineOperator.gameIdentity.Black);
                    sendTextMessage(operator.getSession(),"gamerIdentity."+operatorGameIdentity);
                    sendTextMessage(opponent.getSession(),"gamerIdentity."+!operatorGameIdentity);
                }
            }
        }
    }

    private static Room parseRoom(String roomJson) throws IOException {
        return JSON.parseObject(roomJson,Room.class);
    }

    @OnMessage
    public void onBinary(byte[] info, Session session){
        //传入的message也为两部分组成，但是需要对二进制数据解析后才能获得，格式与字符串的一致
        String information = new String(info);
        if("guestResponseToJoin".equals(information.split("\\.")[0])){
            //房客客户端响应加入房间的请求，此时携带的数据为用户的数据，直接将数据传递给房主即可
            sendBytesMessage(rooms.get(session.getId()).getOwner().getSession(),info);
        }else if("ownerResponseToJoin".equals(information.split("\\.")[0])){
            //房主客户端响应加入房间的请求，此时携带的数据为用户的数据，直接将数据传递给房客即可
            sendBytesMessage(rooms.get(session.getId()).getOwner().getSession(), info);
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
    public boolean buildRoom(Room room, Session session) {


        //获得房间id
        String RoomId = new GameController().getRoomId();
        room.setRoomId(RoomId);

        //创建操作对象方便使用
        OnlineOperator operator = new OnlineOperator();
        operator.setSession(session);


        if(roomServiceImp.buildRoom(room)){
            operator.setIdentity(OnlineOperator.roomIdentity.Owner);
            //将room对象添加到存放room的集合中
            InternalRoom tmpRoom = new InternalRoom();
            tmpRoom.setRoom(room);
            tmpRoom.setOwner(operator);
            //存放到房间集合中
            rooms.put(RoomId, tmpRoom);
            return true;
        }
        return false;
    }
    public void joinRoom(int guestId, Room room, Session session) {

        //获取onlineOperator
        OnlineOperator operator = new OnlineOperator();
        operator.setSession(session);



        //当且仅当房间内没有别的guest时
        if(rooms.get(room.getRoomId()) == null && rooms.get(room.getRoomId()) != null){


            if(roomServiceImp.joinRoom(room,guestId)){


                operator.setIdentity(OnlineOperator.roomIdentity.Guest);


                //向数据库中添加用户数据成功，此时向房主客户端发送加房通知，并向房客发送加房成功通知
                room = roomServiceImp.selectRoom(room.getRoomId());


                //向房主发送加房通知，通知格式为Json字符串包含房客信息
                OnlineOperator opponent = rooms.get(room.getRoomId()).getOwner();
                sendTextMessage(opponent.getSession(), "joinInNotice");
                //向房客发送加房成功通知
                sendTextMessage(operator.getSession(), "joinInSuccess");
                //将房客数据存放到集合中
                rooms.get(room.getRoomId()).setGuest(operator);
            }
        }
    }

    /**
     * 实现更改房间信息功能
     * @param room
     */
    public void updateRoom(Room room, Session session) {

        //同过id获取internalRoom对象
        InternalRoom beforeRoom = rooms.get(room.getRoomId());


        //更新房间的状况
        if(roomServiceImp.updateRoom(room)){
            if(!room.isPublic() && beforeRoom.getCondition().equals(RoomCondition.Waiting)){

                beforeRoom.setCondition(RoomCondition.Lock);

            }else if(room.isPublic() && beforeRoom.getCondition().equals(RoomCondition.Lock)){
                if(beforeRoom.getGuest() != null){

                    beforeRoom.setCondition(RoomCondition.Full);

                }else{


                    beforeRoom.setCondition(RoomCondition.Waiting);


                }
            }
            if(room.getGuestId() != 0){


                sendTextMessage(session, "roomInfo."+JSON.toJSONString(room));


            }
        }else{

            sendTextMessage(session, "updateRoomFalse");

        }
    }
    public void exitRoom(Room room,Session session) {

        OnlineOperator operator = rooms.get(room.getRoomId()).getOwner();


        if(rooms.get(room.getRoomId()).getOwner().getIdentity().equals(OnlineOperator.roomIdentity.Owner)){
            //如果是房主的话
            if(room.getGuestId() != 0){


                //如果房间中有房客的话，将房客成为房主,将原房主的信息清空
                rooms.get(operator.getSession().getId()).setOwner(rooms.get(operator.getSession().getId()).getGuest());
                rooms.get(operator.getSession().getId()).setGuest(null);
                operator.setIdentity(OnlineOperator.roomIdentity.Owner);


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


                OnlineOperator opponent = rooms.get(room.getRoomId()).getGuest();

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

            OnlineOperator operator = rooms.get(room.getRoomId()).getOwner();

            //将本地的Owner信息清空
            operator.setIdentity(null);
            rooms.remove(operator.getSession().getId());


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
    public void onError(Throwable throwable) {
        System.err.println("发生错误: " + throwable.getMessage());
    }


}
