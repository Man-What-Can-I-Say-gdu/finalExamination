package com.pumpkin.entity;

import com.ibm.icu.text.SimpleDateFormat;

public class HistoricGame {
    private String gameId;
    private String startTime;
    private String endTime;
    /**
     * 标记赢家，0为黑棋赢，1为白棋赢
     */
    private boolean winnerType;
    /**
     * 主攻手（：黑方）id
     */
    private int offensiveId;
    /**
     * 后攻手（：白棋）id
     */
    private int defensiveId;

    public int getDefensiveId() {
        return defensiveId;
    }

    public void setDefensiveId(int defensiveId) {
        this.defensiveId = defensiveId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getOffensiveId() {
        return offensiveId;
    }

    public void setOffensiveId(int offensiveId) {
        this.offensiveId = offensiveId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isWinnerType() {
        return winnerType;
    }

    public void setWinnerType(boolean winnerType) {
        this.winnerType = winnerType;
    }

    public HistoricGame() {
    }

    public HistoricGame(int defensiveId, String endTime, String gameId, int offensiveId, String startTime, boolean winnerType) {
        this.defensiveId = defensiveId;
        this.endTime = endTime;
        this.gameId = gameId;
        this.offensiveId = offensiveId;
        this.startTime = startTime;
        this.winnerType = winnerType;
    }
}
