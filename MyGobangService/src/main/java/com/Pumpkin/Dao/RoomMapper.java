package com.Pumpkin.Dao;

import com.Pumpkin.entity.Room;

import java.util.ArrayList;

public interface RoomMapper {
    /**
     * 创建房间
     * @param room
     * @return
     */
    public boolean insertRoom(Room room);

    /**
     * 修改房间信息
     */
    public boolean updateRoom(Room room);

    /**
     * 通过房价id查找房间
     */
    public Room selectRoomById(String RoomId);

    /**
     * 获取所有房间的信息
     */
    public ArrayList<Room> selectAllRoom();

    /**
     * 删除房间
     */
    public boolean deleteRoomById(String RoomId);

    /**
     * 添加guestId
     */
    public boolean updateRoomWithguestId(Room room, int guestId);

    public boolean updateRoomOutOfGuestId(Room room);

    /**
     * 交换房主房客id
     */
    public boolean exchangeGuestAndOwner(Room room);
}
