package com.Pumpkin.Controller;

import com.Pumpkin.Service.ServiceImp.RoomServiceImp;
import com.Pumpkin.entity.Gamer;
import com.Pumpkin.entity.Room;
import com.alibaba.fastjson2.JSON;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint("/Room/")
public class RoomServerEndpoint {
    /**
     * 存储客户端的url和session
     */
    private static Map<Integer, Session> roomOwner = new ConcurrentHashMap<Integer, Session>();
    private static Map<Integer,Session> roomGuest = new ConcurrentHashMap<Integer, Session>();
    private static Map<String, Session> clients = new ConcurrentHashMap<String,Session>();
    private static RoomServiceImp roomServiceImp = new RoomServiceImp();
    private static Room room = new Room();
    private Session session;

    @OnOpen
    public void OnOpen(Session session,String SessionURL){
        //sessionURL是唯一id，在建立连接后将数据添加到clients
        clients.put(SessionURL,session);
        this.session = session;
        sendTextMessage(this.session,"success");
    }

    @OnMessage
    public void OnText(String info) {
        //经过设计的消息，需要进行解析获得指令并执行相应的操作
        String[] infoPart = info.split("\\.", 2);
        try {
            //对room数据进行解析
            room = parseRoom(new String(Hex.decodeHex(infoPart[1].toCharArray())));
            //对指令进行解析
            infoPart[0] = new String(Hex.decodeHex(infoPart[0].toCharArray()));
        } catch (IOException | DecoderException e) {
            throw new RuntimeException(e);
        }
        if("buildRoom".equals(infoPart[0])){

        }else if("joinRoom".equals(infoPart[0])){

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
            sendBytesMessage(roomOwner.get(room.getOwnerId()),info);
        }else if("ownerResponseToJoin".equals(information.split("\\.")[0])){
            //房主客户端响应加入房间的请求，此时携带的数据为用户的数据，直接将数据传递给房客即可
            sendBytesMessage(roomGuest.get(room.getGuestId()),info);
        }
    }

    /**
     * 处理创建房间请求，并将房主id和session关联，方便后续调用
     * @param room
     * @return
     */
    public boolean buildRoom(Room room) {
        if(roomServiceImp.buildRoom(room)){
            roomOwner.put(room.getOwnerId(),this.session);
            return true;
        }
        return false;
    }
    public void joinRoom(int guestId, Room room) {
        if(roomServiceImp.joinRoom(room,guestId)){
            roomGuest.put(room.getGuestId(),this.session);
            //向数据库中添加用户数据成功，此时向房主客户端发送加房通知，并向房客发送加房成功通知
            room = roomServiceImp.selectRoom(room.getRoomId());
            //向房主发送加房通知，通知格式为Json字符串包含房客信息
            sendTextMessage(this.session,"JoinInNotice");
            //向房客发送加房成功通知
            sendTextMessage(this.session,"success");
            //发送完后等待客户端相响应数据
        }
    }

    /**
     * 实现更改房间信息功能
     * @param room
     */
    public void updateRoom(Room room) {
        if(roomServiceImp.updateRoom(room)){
            //向房主发送修改信息
            sendTextMessage(this.session,JSON.toJSONString(room));
            if(room.getGuestId() != 0){
                sendTextMessage(roomGuest.get(room.getGuestId()),JSON.toJSONString(room));
            }
        }else{
            sendTextMessage(this.session,"false");
        }
    }
    public void exitRoom(Room room) {
        if(this.session.equals(roomOwner.get(room.getOwnerId()))){
            //如果是房主的话
            if(room.getGuestId() != 0){
                //如果房间中有房客的话，将房客成为房主
                //将房客的session转移到roomOwner
                roomOwner.remove(room.getOwnerId());
                roomOwner.put(room.getGuestId(),roomGuest.get(room.getGuestId()));
                roomOwner.remove(room.getGuestId());
                //清除数据库的owner信息，将guest的信息转移到Owner上
                roomServiceImp.exchangeGuestAndOwner(room);
                roomServiceImp.deleteGuest(room);
                room = roomServiceImp.selectRoom(room.getRoomId());
                sendTextMessage(roomOwner.get(room.getOwnerId()), "OwnerExit");
            }else{
                //如果房间中没有房客的话，直接摧毁房间
                destoryRoom(room);
            }
        }else if(this.session.equals(roomGuest.get(room.getGuestId()))){
            //如果是房客的话，清除房客信息
            if(roomServiceImp.deleteGuest(room)){
                roomGuest.remove(room.getGuestId());
                //向服务器发送房客退出的信息
                sendTextMessage(roomOwner.get(room.getOwnerId()),"GuestOutOfRoom");
            }

        }
    }
    public void destoryRoom(Room room) {
        //在数据库清空房间信息
        if(roomServiceImp.destoryRoom(room.getRoomId())){
            //将本地的Owner信息清空
            roomOwner.remove(room.getOwnerId());
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

}
