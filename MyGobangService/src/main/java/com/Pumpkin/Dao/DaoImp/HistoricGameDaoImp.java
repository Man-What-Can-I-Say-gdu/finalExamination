package com.Pumpkin.Dao.DaoImp;

import com.Pumpkin.Dao.HistoricGameDao;
import com.Pumpkin.DataBasePool.ConnectionPool;
import com.Pumpkin.entity.HistoricGame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class HistoricGameDaoImp implements HistoricGameDao {
    /**
     * 通过传入的Game对象创建游戏
     * @param historicGame
     * @return
     */
    @Override
    public boolean buildGame(HistoricGame historicGame) {
        String SQL = "insert into historicGame(gameId,startTime,offensiveId,defensiveId) values (?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = ConnectionPool.GetConnection();
            PreparedStatement ps = connection.prepareStatement(SQL);
            ps.setString(1, historicGame.getGameId());
            ps.setString(2, historicGame.getStartTime());
            ps.setInt(3, historicGame.getOffensiveId());
            ps.setInt(4, historicGame.getDefensiveId());
            int result = ps.executeUpdate();
            ps.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 修改胜利方的数据即可
     * @param historicGame
     * @return
     */
    @Override
    public boolean saveGame(HistoricGame historicGame) {
        String SQL = "update HistoricGame set WinnerType = ? where gameId = ?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement ps = connection.prepareStatement(SQL);
            ps.setBoolean(1, historicGame.isWinnerType());
            ps.setString(2, historicGame.getGameId());
            int result = ps.executeUpdate();
            ps.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查找指定游戏id的对局
     * @param historicGame
     * @return
     */
    @Override
    public HistoricGame selectGame(HistoricGame historicGame) {
        String SQL = "select * from HistoricGame where gameId = ?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement ps = connection.prepareStatement(SQL);
            ps.setString(1, historicGame.getGameId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                historicGame.setGameId(rs.getString("gameId"));
                historicGame.setStartTime(rs.getString("startTime"));
                historicGame.setOffensiveId(rs.getInt("offensiveId"));
                historicGame.setDefensiveId(rs.getInt("defensiveId"));
                historicGame.setWinnerType(rs.getBoolean("WinnerType"));
            }
            ps.close();
            ConnectionPool.RecycleConnection(connection);
            return historicGame;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<HistoricGame> selectAllGameById(int id) {
        ArrayList<HistoricGame> games = selectAllGamesByDefensiveId(id);
        games.addAll(selectAllGamesByOffensiveId(id));
        return games;
    }

    public ArrayList<HistoricGame> selectAllGamesByOffensiveId(int id) {
        ArrayList<HistoricGame> historicGames = new ArrayList<>();
        String SQL = "select * from HistoricGame where offensiveId = ?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement ps = connection.prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HistoricGame historicGame = new HistoricGame();
                historicGame.setGameId(rs.getString("gameId"));
                historicGame.setStartTime(rs.getString("startTime"));
                historicGame.setOffensiveId(rs.getInt("offensiveId"));
                historicGame.setDefensiveId(rs.getInt("defensiveId"));
                historicGame.setWinnerType(rs.getBoolean("WinnerType"));
                historicGame.setEndTime(rs.getString("endTime"));
                historicGames.add(historicGame);
            }
            ps.close();
            ConnectionPool.RecycleConnection(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return historicGames;
    }
    public ArrayList<HistoricGame> selectAllGamesByDefensiveId(int id) {
        ArrayList<HistoricGame> historicGames = new ArrayList<>();
        String SQL = "select * from HistoricGame where defensiveId = ?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement ps = connection.prepareStatement(SQL);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HistoricGame historicGame = new HistoricGame();
                historicGame.setGameId(rs.getString("gameId"));
                historicGame.setStartTime(rs.getString("startTime"));
                historicGame.setOffensiveId(rs.getInt("offensiveId"));
                historicGame.setDefensiveId(rs.getInt("defensiveId"));
                historicGame.setWinnerType(rs.getBoolean("WinnerType"));
                historicGame.setEndTime(rs.getString("endTime"));
                historicGames.add(historicGame);
            }
            ps.close();
            ConnectionPool.RecycleConnection(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return historicGames;
    }


    @Override
    public ArrayList<HistoricGame> selectAllGameInSpecialTIme(String specialTIme) {
        ArrayList<HistoricGame> games = new ArrayList<>();
        String SQL = "select * from HistoricGame where specialTIme like ?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement ps = connection.prepareStatement(SQL);
            ps.setString(1, "'%" + specialTIme+"'");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HistoricGame historicGame = new HistoricGame();
                historicGame.setGameId(rs.getString("gameId"));
                historicGame.setStartTime(rs.getString("startTime"));
                historicGame.setOffensiveId(rs.getInt("offensiveId"));
                historicGame.setDefensiveId(rs.getInt("defensiveId"));
                historicGame.setWinnerType(rs.getBoolean("WinnerType"));
                historicGame.setEndTime(rs.getString("endTime"));
                games.add(historicGame);
            }
            ps.close();
            ConnectionPool.RecycleConnection(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return games;
    }
}
