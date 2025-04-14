package com.pumpkin.entity;

public class Room {
    /**
     * 房间号
     */
    private String roomId;
    /**
     * 是否公开
     */
    private boolean isPublic;
    /**
     * 房间密码
     */
    private String password;
    /**
     * 房主id
     */
    private int ownerId;
    /**
     * 玩家id
     */
    private int guestId;

    public Room(String roomId, boolean isPublic, String password, int ownerId, int guestId) {
        this.roomId = roomId;
        this.isPublic = isPublic;
        this.password = password;
        this.ownerId = ownerId;
        this.guestId = guestId;
    }

    public Room() {
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }
}
