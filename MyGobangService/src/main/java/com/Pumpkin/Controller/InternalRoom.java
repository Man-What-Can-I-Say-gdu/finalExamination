package com.Pumpkin.Controller;

import com.Pumpkin.entity.OnlineOperator;
import com.Pumpkin.entity.Room;
import com.Pumpkin.entity.RoomCondition;

import javax.websocket.Session;

/**
 * 房间管理类
 */
public class InternalRoom {


    public RoomCondition getCondition() {
        return condition;
    }

    public void setCondition(RoomCondition condition) {
        this.condition = condition;
    }

    /**
     * 房间信息
     */
    private Room room;
    /**
     * 房主
     */
    private OnlineOperator owner;
    /**
     * 房客
     */
    private OnlineOperator guest;

    /**
     * 房间状态
     */
    public RoomCondition condition;

    /**
     * 向房间里的人进行广播
     *
     * @param message
     */
    public void broadcastMessage(String message) {
        sendToOwner(owner.getSession(), message);
        sendToGuest(guest.getSession(), message);
    }

    /**
     * 发送消息给owner
     *
     * @param session
     */
    public void sendToOwner(Session session, String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }

    /**
     * 发送消息给guest
     *
     * @param session
     */
    public void sendToGuest(Session session, String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }


    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public OnlineOperator getOwner() {
        return owner;
    }

    public void setOwner(OnlineOperator owner) {
        this.owner = owner;
    }

    public OnlineOperator getGuest() {
        return guest;
    }

    public void setGuest(OnlineOperator guest) {
        this.guest = guest;
    }
}





