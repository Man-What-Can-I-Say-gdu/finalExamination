package com.pumpkin.controller.ControllerImp;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ibm.icu.text.SimpleDateFormat;
import com.pumpkin.Service.ServiceImp.ChessStyleServiceImp;
import com.pumpkin.Service.ServiceImp.HistoricGameServiceImp;
import com.pumpkin.Service.ServiceImp.UserServiceImp;
import com.pumpkin.entity.Chess;
import com.pumpkin.entity.HistoricGame;
import com.pumpkin.entity.Room;
import com.pumpkin.entity.User;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * 负责处理前端发送的数据
 */
@ServerEndpoint("/MyGobang/GameImp")
public class frontServerEndpoint {
    /**
     * 前端与后端的连接
     */
    Session session;
    Room room = new Room();
    User owner = new User();
    User guest = new User();
    UserServiceImp userServiceImp = new UserServiceImp();
    Chess chess = new Chess();
    ChessStyleControllerImp chessStyleControllerImp = new ChessStyleControllerImp();
    /**
     * 正在操作的User
     */
    User operateUser = new User();

    /**
     * 用来记录棋局的id
     */
    String chessStyleId;

    /**
     * 已经走的步数
     */
    int steps;

    /**
     * 棋的花色
     */
    boolean isBlack;

    /**
     * 记录棋局的信息
     */
    HistoricGame game = new HistoricGame();
    HistoricGameServiceImp historicGameServiceImp = new HistoricGameServiceImp();

    @OnOpen
    public void Onopen(Session session,int id) {
        this.session = session;
        this.operateUser = userServiceImp.selectUserById(id);
    }

    /**
     * 处理前端发送的字符串消息
     */
    @OnMessage
    public void onStringInfo(String info){
        //info包含两种格式：1.由指令类型+”.“+数据组成，2.单纯的指令类型
        String[] infoParts = info.split("\\.");
        if (infoParts.length == 2) {
            //此时为第一种
            if ("buildRoom".equals(infoParts[0])) {
                //创建房间的请求,此处应为异步请求，前端在完成表单数据填写后利用表单填写的数据直接进入
                room = parseToRoom(infoParts[1]);
                if(room == null){
                    sendStringInfo("RoomInfoError");
                }
            }else if("joinRoom".equals(infoParts[0])){
                //加入房间的请求，包含room的数据,主要用于取出guestId
                room.setGuestId(parseToRoom(infoParts[1]).getGuestId());
                if(room.getGuestId() == 0){
                    sendStringInfo("GuestIdError");
                }
            }else if("updateRoom".equals(infoParts[0])){
                //修改房间的请求,遍历传入的room数据，将room数据由进行更改的部分进行删除
                Room tmpRoom = parseToRoom(infoParts[1]);
                if(tmpRoom.isPublic()){
                    if(!room.isPublic()){
                        //原先不公开，此处更改为公开，则将密码设为空
                        room.setPassword(null);
                    }
                }else{
                    if(room.isPublic()){
                        //原先公开，此处改为不公开更改密码
                        room.setPassword(tmpRoom.getPassword());
                    }else{
                        //原先不公开，此处为不公开，确定密码是否更改
                        if(tmpRoom.getPassword() != null && !room.getPassword().equals(tmpRoom.getPassword())){
                            //密码不为空且与原先密码不同的情况
                            room.setPassword(tmpRoom.getPassword());
                        }
                    }
                }
            }else if("chessing".equals(infoParts[0])){
                //得到棋子数据
                chess = parseToChess(infoParts[1]);
                if(chess == null){
                    //棋子为空，说明数据传输出现问题
                    sendStringInfo("ChessingError");
                }else{
                    //将棋子数据传递到chess中
                    if(chessStyleControllerImp.insertChess(chessStyleId,chess)){
                        //获取下棋后棋子的位置的结果
                        int end = chessStyleControllerImp.isWin(chessStyleId,chess);
                    }
                }
            }else if("startGame".equals(infoParts[0])){
                //解析服务器发送的开始时间和对局id
                game = JSON.parseObject(infoParts[1], HistoricGame.class);
                chessStyleId = game.getGameId();
            }else if("lost".equals(infoParts[0])){
                //游戏结束，中继服务器接收到响应指令后向服务器提交结束时间
                HistoricGame tmp = JSON.parseObject(infoParts[1], HistoricGame.class);
                game.setEndTime(tmp.getEndTime());
                game.setWinnerType(isBlack ? true : false);
                historicGameServiceImp.insertHistoricGame(game);
            }else if("victory".equals(infoParts[0])){
                //游戏结束，中继服务器接收到响应指令后向服务器提交结束时间
                HistoricGame tmp = JSON.parseObject(infoParts[1], HistoricGame.class);
                game.setEndTime(tmp.getEndTime());
                game.setWinnerType(isBlack ? false : true);
                historicGameServiceImp.insertHistoricGame(game);
            }
        }else if(infoParts.length == 1){
            //此时为第二种
            switch (info) {
                case "ownerExitRoom":
                    //房主离开房间的请求,先比对自己是不是房主
                    if (room.getOwnerId() == operateUser.getId()) {
                        //如果操作的用户为房主，删除房间数据
                        clearRoomData();
                        clearUserData(guest);
                        owner = null;
                    } else if (room.getGuestId() == operateUser.getId()) {
                        //操作用户为房客，此时房客成为房主
                        room.setOwnerId(operateUser.getId());
                        room.setGuestId(0);
                        owner = operateUser;
                        guest = null;
                    }
                    break;
                case "guestExitRoom":
                    if (room.getGuestId() == operateUser.getId()) {
                        //操作对象为房客
                        clearRoomData();
                        clearUserData(owner);
                        guest = null;
                    } else if (room.getOwnerId() == operateUser.getId()) {
                        //操作对象为房主，则清除房客数据
                        clearUserData(guest);
                        room.setGuestId(0);
                    }
                    break;
                case "joinInNotice":
                    //房客申请进入房间的请求，传递房主数据的json字符串
                    sendStringInfo(JSON.toJSONString(operateUser));
                    break;
                case "joinInSuccess":
                    sendStringInfo(JSON.toJSONString(operateUser));
                    break;
                case "withdraw":
                    //游戏过程中，操作用户使用悔棋，将前两步的数据清空
                    chessStyleControllerImp.Repentance(chessStyleId, chess, isBlack);
                    break;
                case "third":
                    //三三禁手
                    break;
                case "forth":
                    //四四禁手
                    break;
                case "forbid":
                    //长连禁手
                    break;
            }
        }
    }

    /**
     * 解析Room数据的json字符串
     * @param roomInfo
     * @return
     */
    private Room parseToRoom(String roomInfo){
        if(roomInfo == null){
            return null;
        }
        return JSON.parseObject(roomInfo,Room.class);
    }
    /**
     * 传递字符串数据
     */
    public void sendStringInfo(String info){
        try {
            session.getBasicRemote().sendText(info);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 传递二进制数据
     */
    public void sendBinaryInfo(byte[] bytes){
        try {
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * 清空房间数据
     */
    private void clearRoomData(){
        room.setRoomId(null);
        room.setPublic(false);
        room.setGuestId(0);
        room.setOwnerId(0);
        room.setPassword(null);
    }
    /**
     * 清除指定用户数据
     */
    private void clearUserData(User user){
        user.setEmail(null);
        user.setPassword(null);
        user.setId(0);
        user.setName(null);
        user.setPhoneNumber(null);
        user.setSalt(null);

    }

    /**
     * 解析棋子数据的JSON字符串
     * @param chessInfo
     * @return
     */
    private Chess parseToChess(String chessInfo){
        if(chessInfo == null){
            return null;
        }
        return JSON.parseObject(chessInfo,Chess.class);
    }

}
