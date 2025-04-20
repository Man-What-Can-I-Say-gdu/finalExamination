package com.Pumpkin.Service.ServiceImp;

import com.Pumpkin.Dao.DaoImp.RoomDaoImp;
import com.Pumpkin.Service.RoomService;
import com.Pumpkin.entity.Room;

public class RoomServiceImp implements RoomService {
    RoomDaoImp roomDao = new RoomDaoImp();
    @Override
    public boolean buildRoom(Room room) {
        return roomDao.insertRoom(room);
    }

    @Override
    public Room selectRoom(String RoomId) {
        return roomDao.selectRoomById(RoomId);
    }

    @Override
    public boolean joinRoom(Room room, int guestId) {
        return roomDao.updateRoomWithguestId(room,guestId);
    }

    @Override
    public boolean updateRoom(Room room) {
        return roomDao.updateRoom(room);
    }

    @Override
    public boolean destoryRoom(String roomId) {
        return roomDao.deleteRoomById(roomId);
    }

    @Override
    public boolean deleteGuest(Room room) {
        return roomDao.updateRoomOutOfGuestId(room);
    }
}
