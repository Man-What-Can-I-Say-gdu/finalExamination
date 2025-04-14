package com.pumpkin.entity;

public class chessStyle {
    private String GameId;
    private String FinalStyleId;
    private String RoomId;

    public chessStyle() {
    }

    public chessStyle(String gameId, String finalStyleId, String roomId) {
        GameId = gameId;
        FinalStyleId = finalStyleId;
        RoomId = roomId;
    }

    public String getGameId() {
        return GameId;
    }

    public void setGameId(String gameId) {
        GameId = gameId;
    }

    public String getFinalStyleId() {
        return FinalStyleId;
    }

    public void setFinalStyleId(String finalStyleId) {
        FinalStyleId = finalStyleId;
    }

    public String getRoomId() {
        return RoomId;
    }

    public void setRoomId(String roomId) {
        RoomId = roomId;
    }
}
