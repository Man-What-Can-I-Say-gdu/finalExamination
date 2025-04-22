package com.pumpkin.controller.ControllerImp;


import com.alibaba.fastjson2.JSON;
import com.pumpkin.entity.Room;
import com.pumpkin.entity.User;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;

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
    /**
     * 正在操作的User
     */
    User operateUser = new User();
    @OnOpen
    public void Onopen(Session session) {
        this.session = session;
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
                    if(room.isPublic()){
                        //原先就是公开的，此处不做改变
                    } else {
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
            }
        }else if(infoParts.length == 1){
            //此时为第二种
            if("ownerExitRoom".equals(info)){
                //房主离开房间的请求,先比对自己是不是房主
                if(room.getOwnerId() == operateUser.getId()){
                    //如果操作的用户为房主，删除房间数据
                    clearRoomData();
                    clearUserData(guest);
                    owner = null;
                }else if(room.getOwnerId() == operateUser.getId()){
                    //操作用户为房客，此时房客成为房主
                    room.setOwnerId(operateUser.getId());
                    room.setGuestId(0);
                    owner = operateUser;
                    guest = null;
                }
            }else if("guestExitRoom".equals(info)){
                if(room.getGuestId() == operateUser.getId()){
                    //操作对象为房客
                    clearRoomData();
                    clearUserData(owner);
                    guest = null;
                }else if(room.getOwnerId() == operateUser.getId()){
                    //操作对象为房主，则清除房客数据
                    clearUserData(guest);
                    room.setGuestId(0);
                }
            }
        }
    }


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
}
