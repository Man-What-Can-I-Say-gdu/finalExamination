package com.Pumpkin.entity;

import javax.websocket.Session;

public class OnlineOperator {
    public enum roomIdentity{
        Guest,
        Owner
    }
    public enum gameIdentity{
        Black,
        White
    }
    /**
     * 存放在线用户的session
     */
    private Session session;
    /**
     * 存放在线用户在房间中的身份
     */
    private roomIdentity identity;
    /**
     * 存放在线用户在游戏中的身份
     */
    private gameIdentity gameIdentity;

    /**
     * 用户id
     */
    private int operatorId;

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public OnlineOperator.gameIdentity getGameIdentity() {
        return gameIdentity;
    }

    public void setGameIdentity(OnlineOperator.gameIdentity gameIdentity) {
        this.gameIdentity = gameIdentity;
    }

    public roomIdentity getIdentity() {
        return identity;
    }

    public void setIdentity(roomIdentity identity) {
        this.identity = identity;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public OnlineOperator() {
    }

    public OnlineOperator(Session session, roomIdentity identity, OnlineOperator.gameIdentity gameIdentity, int operatorId) {
        this.session = session;
        this.identity = identity;
        this.gameIdentity = gameIdentity;
        this.operatorId = operatorId;
    }
}
