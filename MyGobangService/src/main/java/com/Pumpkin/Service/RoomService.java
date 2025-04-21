package com.Pumpkin.Service;

import com.Pumpkin.entity.Room;

public interface RoomService {
    /**
     * 创建房间
     */
    public boolean buildRoom(Room room);

    /**
     * 根据id查找房间
     */
    public Room selectRoom(String RoomId);

    /**
     * 将房客加入房间
     */
    public boolean joinRoom(Room room,int guestId);

    /**
     * 修改房间信息
     */
    public boolean updateRoom(Room room);

    /**
     * 销毁房间
     */
    public boolean destoryRoom(String roomId);

    /**
     * 删除guestId
     * @param room
     * @return
     */
    public boolean deleteGuest(Room room);

    /**
     * 将guestId和ownerId翻转
     */
    public boolean exchangeGuestAndOwner(Room room);
}
