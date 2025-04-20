package com.pumpkin.Dao;

import com.pumpkin.entity.Room;

public interface RoomMapper {


    /**
     * 创建房间数据
     * @param room 创建的room对象
     */
    public Room insertRoom(Room room);

    /**
     * 获取房间数据
     */
    public Room selectRoomById(int id);

}
