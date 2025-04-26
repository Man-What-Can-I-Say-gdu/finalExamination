package com.Pumpkin.Dao.DaoImp;

import com.Pumpkin.Controller.GameController;
import com.Pumpkin.Dao.RoomMapper;
import com.Pumpkin.DataBasePool.ConnectionPool;
import com.Pumpkin.entity.Room;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RoomDaoImp implements RoomMapper {

    @Override
    public boolean insertRoom(Room room) {
        String sql = "insert into room(RoomId,IsPublic,password,ownerId) values(?,?,?,?)";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, room.getRoomId());
            ps.setBoolean(2,room.isPublic());
            ps.setString(3, room.getPassword());
            ps.setInt(4, room.getOwnerId());
            int result = ps.executeUpdate();
            ps.close();
            ConnectionPool.RecycleConnection(connection);
            //返回结果影响的行数
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateRoom(Room room) {
        String SQL = "update room set IsPublic=?,password=?where RoomId=?";
        try(Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            ){
            preparedStatement.setBoolean(1, room.isPublic());
            preparedStatement.setString(2, room.getPassword());
            preparedStatement.setString(3, room.getRoomId());
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Room selectRoomById(String RoomId) {
        Room room = new Room();
        String sql = "select * from room where RoomId=?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, RoomId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                room.setRoomId(rs.getString("RoomId"));
                room.setPublic(rs.getBoolean("IsPublic"));
                room.setPassword(rs.getString("password"));
                room.setOwnerId(rs.getInt("ownerId"));
                room.setGuestId(rs.getInt("guestId"));
            }
            ps.close();
            ConnectionPool.RecycleConnection(connection);
            return room;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Room> selectAllRoom() {
        String SQL = "select * from room";
        ArrayList<Room> rooms = new ArrayList<>();
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getString("RoomId"));
                room.setPublic(rs.getBoolean("IsPublic"));
                room.setPassword(rs.getString("password"));
                room.setOwnerId(rs.getInt("ownerId"));
                room.setGuestId(rs.getInt("guestId"));
                rooms.add(room);
            }
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return rooms;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean deleteRoomById(String RoomId) {
        String sql = "delete from room where RoomId=?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedstatement = connection.prepareStatement(sql);
            preparedstatement.setString(1, RoomId);
            int result = preparedstatement.executeUpdate();
            preparedstatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateRoomWithguestId(Room room,int guestId) {
        Room requireRoom = selectRoomById(room.getRoomId());
        if(requireRoom.getGuestId() != 0){
            return false;
        }
        String SQL = "update room set guestId=? where RoomId=?";
        try(Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL)){
                preparedStatement.setInt(1, room.getGuestId());
                preparedStatement.setString(2, room.getRoomId());
                int result = preparedStatement.executeUpdate();
                preparedStatement.close();
                ConnectionPool.RecycleConnection(connection);
                return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateRoomOutOfGuestId(Room room){
        String SQL = "update room set guestId=? where RoomId=?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, 0);
            preparedStatement.setString(2, room.getRoomId());
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean exchangeGuestAndOwner(Room room) {
        String SQL = "update room set guestId=?, ownerId=? where RoomId = ?";
        try(Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.setInt(1,room.getOwnerId());
            preparedStatement.setInt(2,room.getGuestId());
            preparedStatement.setString(3,room.getRoomId());
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result>0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
