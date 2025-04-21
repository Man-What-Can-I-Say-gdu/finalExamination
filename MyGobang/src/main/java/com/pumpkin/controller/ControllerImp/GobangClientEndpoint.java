package com.pumpkin.controller.ControllerImp;


import com.alibaba.fastjson2.JSON;
import com.pumpkin.Service.ServiceImp.UserServiceImp;
import com.pumpkin.entity.Room;
import com.pumpkin.entity.User;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;


/**
 * 客户端的端点类，进行调用
 */
@ClientEndpoint
public class GobangClientEndpoint {
    private UserServiceImp userServiceImp = new UserServiceImp();
    private Socket socket;
    /**
     * 用户本人的数据
     */
    private User user;
    /**
     * 如果用户时房主才启用：房客数据
     */
    private User guest;
    /**
     * 如果用户是房客才使用：房主数据
     */
    private User owner;
    private Session session;
    private Room room;
    //private String URL = "101.37.135.39";
    //private int PORT = 8081;


    public GobangClientEndpoint(User user) {
        this.user = user;
    }

    @OnMessage
    public void onStringMessage(String message){
        String[] textMessage = message.split("\\.");
        if(textMessage.length != 2){
            //此时字符串为指示消息，只需要只需要读取并获取内容即可
            if("joinInNotice".equals(message)){
                //加入房间的指示,此时客户端需要准备好房主信息并传出，房主信息可能包含图片（头像但未添加），因此采用二进制传递
                sendUserInfo(user.getId());
            }else if("joinInSuccess".equals(message)){
                //发送给客户端的成功加入房间的请求，接下来就是将用户数据发送给房客进行同步
                sendUserInfo(user.getId());
            }else if("updateRoomFalse".equals(message)){
                //更改房间数据失败，接下来要返回给网页失败的数据
            }else if("GuestOutOfRoom".equals(message)){
                //房客退出房间，清空房客信息
                resetUser(guest);
                room.setGuestId(0);
            }else if("OwnerExit".equals(message)){
                //房主成为房间，房客成为房主
                room.setOwnerId(user.getId());
                room.setGuestId(0);
                owner = user;
                resetUser(guest);
            }
        }else if(textMessage.length == 2){
            //此时字符串为JSON字符串,用于存放数据，需要进行解析
            if("roomInfo".equals(textMessage[0])) {
                //此时传入room的数据，只需要对数据进行解析并传输到前端即可
                room = JSON.parseObject(textMessage[1],Room.class);

            }
        }
    }

    private void sendUserInfo(int id) {
        user = userServiceImp.selectUserById(id);
        //将user转化为二进制数据并传输，在此之前要先转换为JSON字符串
        String ownerInfoString = JSON.toJSONString(user);
        sendBinary(hex2byte(ownerInfoString));
    }

    @OnMessage
    public void onBinaryMessage(byte[] message){

    }

    /**
     * 将字符串转化为二进制数组
     * @param string
     * @return
     */
    public byte[] hex2byte(String string){
        if(string == null){
            return null;
        }
        string = string.trim();
        int len = string.length();

        if(len == 0||len % 2 ==1){
            return null;
        }

        byte[] b = new byte[len/2];
        try{
            for(int i = 0;i < len; i += 2){
                b[i/2] = (byte) Integer.decode("0X"+string.substring(i,i+2)).intValue();

            }
            return b;
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 二进制转字符串
     */
    public String byte2hex(byte[] b){
        StringBuffer sb = new StringBuffer();
        String tmp = "";
        for(int i =0;i < b.length;i++){
            tmp = Integer.toHexString(b[i] & 0XFF);
            if(tmp.length() == 1){
                sb.append("0"+tmp);
            }else{
                sb.append(tmp);
            }
        }
        return sb.toString();
    }


    public void sendBinary(byte[] bytes){
        try {
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendText(String string){
        try {
            session.getBasicRemote().sendText(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void resetUser(User user){
        user.setId(0);
        user.setSalt(null);
        user.setPassword(null);
        user.setName(null);
        user.setEmail(null);
        user.setPhoneNumber(null);
    }
}
