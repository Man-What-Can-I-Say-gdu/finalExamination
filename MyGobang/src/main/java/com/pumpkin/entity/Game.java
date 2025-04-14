package com.pumpkin.entity;

public class Game {
    private String gameId;
    private String gamerId;
    private String beginTime;
    private String endTime;
    private String opponentId;
    private String gameEnd;


    public Game(String gameId, String gamerId, String beginTime, String endTime, String opponentId, String gameEnd) {
        this.gameId = gameId;
        this.gamerId = gamerId;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.opponentId = opponentId;
        this.gameEnd = gameEnd;
    }

    public Game() {
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGamerId() {
        return gamerId;
    }

    public void setGamerId(String gamerId) {
        this.gamerId = gamerId;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(String opponentId) {
        this.opponentId = opponentId;
    }

    public String getGameEnd() {
        return gameEnd;
    }

    public void setGameEnd(String gameEnd) {
        this.gameEnd = gameEnd;
    }

}
