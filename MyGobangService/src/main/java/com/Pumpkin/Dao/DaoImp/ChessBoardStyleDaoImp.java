package com.Pumpkin.Dao.DaoImp;

import com.Pumpkin.Dao.ChessBoardStyleDao;
import com.Pumpkin.DataBasePool.ConnectionPool;
import com.Pumpkin.entity.Chess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChessBoardStyleDaoImp implements ChessBoardStyleDao {
    @Override
    public void buildChessBoardStyle(String GameId) {
        String Sql = "create table ? ( position varchar(7), steps int, type boolean)";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement ps = connection.prepareStatement(Sql);
            ps.setString(1, GameId);
            ps.executeUpdate();
            ps.close();
            ConnectionPool.RecycleConnection(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertChessIntoBoard(String GameId, ArrayList<Chess> chess) {
        String SQL = "insert into ? (position, steps, type) values(?,?,?)";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement ps = connection.prepareStatement(SQL);
            chess.forEach(newChess ->{
                try {
                    ps.setString(1, GameId);
                    ps.setString(2, newChess.getPosition());
                    ps.setInt(3, newChess.getSteps());
                    ps.setBoolean(4, newChess.isStyle());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            ps.close();
            ConnectionPool.RecycleConnection(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<Chess> selectFianlBoardByGameId(String gameId) {
        String SQL = "select * from ?";
        ArrayList<Chess> chess = new ArrayList<>();
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement ps = connection.prepareStatement(SQL);
            ps.setString(1, gameId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Chess chess1 = new Chess();
                chess1.setPosition(rs.getString("position"));
                chess1.setSteps(rs.getInt("steps"));
                chess1.setStyle(rs.getBoolean("type"));
                chess.add(chess1);
            }
            ps.close();
            ConnectionPool.RecycleConnection(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return chess;
    }
}
