package com.pumpkin.Dao.DaoImp;

import DataBasePool.ConnectionPool;
import com.pumpkin.Dao.GamerMapper;
import com.pumpkin.entity.Gamer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GamerDaoImp implements GamerMapper {
    @Override
    public boolean insertGamer(int id) {
        try {
            Connection connection = ConnectionPool.GetConnection();
            String SQL = "insert into gamer(id) value (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Gamer selectAllFromUser(int id) {
        Gamer gamer = null;
        try {
            Connection connection = ConnectionPool.GetConnection();
            String SQL = "select * from gamer where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                gamer.setId(id);
                gamer.setDan(resultSet.getInt("dan"));
                gamer.setSeasonPlayNumb(resultSet.getInt("seasonPlayNumb"));
                gamer.setWinRate(resultSet.getDouble("winRate"));
                gamer.setSeasonWinNumb(resultSet.getInt("seasonWinNumb"));
                gamer.setSumPlayNumb(resultSet.getInt("sumPlayNumb"));
                gamer.setSumWinNumb(resultSet.getInt("sumWinNumb"));
                gamer.setId(resultSet.getInt("id"));
            }
            resultSet.close();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return gamer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateGamerMatchData(Gamer gamer) {
        try {
            Connection connection = ConnectionPool.GetConnection();
            String SQL = "update gamer set winRate=?,sumPlayNumb=?,seasonPlayNumb=?,seasonWinNumber=?,sumWinNumber=?, points=? where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setDouble(1, gamer.getWinRate());
            preparedStatement.setInt(2, gamer.getSumPlayNumb());
            preparedStatement.setInt(3, gamer.getSeasonWinNumb());
            preparedStatement.setInt(4, gamer.getSumWinNumb());
            preparedStatement.setInt(5, gamer.getId());
            preparedStatement.setInt(6,gamer.getPoints());
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteGamerSeasonData(int id) {
        try {
            Connection connection = ConnectionPool.GetConnection();
            String SQL = "update gamer set seasonPlayNumb=0,seasonWinNumber=0 where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, id);
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateGamerDan(Gamer gamer) {
        try {
            Connection connection = ConnectionPool.GetConnection();
            String SQL = "update gamer set dan=? where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setDouble(1, gamer.getDan());
            preparedStatement.setInt(2, gamer.getId());
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
