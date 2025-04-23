package com.pumpkin.Dao.DaoImp;

import DataBasePool.ConnectionPool;
import com.pumpkin.Dao.HistoricGameDao;
import com.pumpkin.Dao.HistoricGameDao;
import com.pumpkin.entity.HistoricGame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class HistoricGameImp implements HistoricGameDao {
    //方便进行对局保存
    private HistoricGame historicGame;

    @Override
    public HistoricGame selectGame(HistoricGame game) {
        String selectSQL = "select * from HistoricGame where gameid = ?";
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1, game.getGameId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                historicGame = new HistoricGame();
                historicGame.setGameId(resultSet.getString("gameid"));
                historicGame.setDefensiveId(resultSet.getInt("defensiveid"));
                historicGame.setEndTime(resultSet.getString("EndTime"));
                historicGame.setStartTime(resultSet.getString("StartTime"));
                historicGame.setOffensiveId(resultSet.getInt("offensiveid"));
                historicGame.setWinnerType(resultSet.getBoolean("winnerType"));
            }
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return historicGame;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<HistoricGame> selectGameById(HistoricGame game) {
        ArrayList<HistoricGame> historicGames = new ArrayList<>();
        getHistoricGamesByOffensiveId(game, historicGames);
        getHistoricGamesByDefensiveId(game, historicGames);
        return historicGames;
    }

    /**
     * 查找作为黑棋的所有游戏记录
     * @param game
     * @param historicGames
     * @return
     */
    private ArrayList<HistoricGame> getHistoricGamesByOffensiveId(HistoricGame game,ArrayList<HistoricGame> historicGames) {
        String SQL = "select * from HistoricGame where offensiveId = ?";

        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, game.getOffensiveId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                historicGame = new HistoricGame();
                historicGame.setGameId(resultSet.getString("gameid"));
                historicGame.setDefensiveId(resultSet.getInt("defensiveid"));
                historicGame.setEndTime(resultSet.getString("EndTime"));
                historicGame.setStartTime(resultSet.getString("StartTime"));
                historicGame.setOffensiveId(resultSet.getInt("offensiveid"));
                historicGame.setWinnerType(resultSet.getBoolean("winnerType"));
                historicGames.add(historicGame);
            }
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return historicGames;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查找作为白棋的所有游戏记录
     * @param game
     * @param historicGames
     * @return
     */
    private ArrayList<HistoricGame> getHistoricGamesByDefensiveId(HistoricGame game,ArrayList<HistoricGame> historicGames) {
        String SQL = "select * from HistoricGame where defensiveId = ?";

        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, game.getOffensiveId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                historicGame = new HistoricGame();
                historicGame.setGameId(resultSet.getString("gameid"));
                historicGame.setDefensiveId(resultSet.getInt("defensiveid"));
                historicGame.setEndTime(resultSet.getString("EndTime"));
                historicGame.setStartTime(resultSet.getString("StartTime"));
                historicGame.setOffensiveId(resultSet.getInt("offensiveid"));
                historicGame.setWinnerType(resultSet.getBoolean("winnerType"));
                historicGames.add(historicGame);
            }
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return historicGames;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean saveGame(HistoricGame game) {
        String saveSQL = "insert into HistoricGame values(?,?,?,?,?,?,?)";
        int result;
        try {
            Connection connection = ConnectionPool.GetConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(saveSQL);
            preparedStatement.setString(1, game.getGameId());
            preparedStatement.setInt(2, game.getDefensiveId());
            preparedStatement.setString(3, game.getEndTime());
            preparedStatement.setString(4, game.getStartTime());
            preparedStatement.setInt(5, game.getOffensiveId());
            preparedStatement.setBoolean(6, game.isWinnerType());
            result = preparedStatement.executeUpdate();
            preparedStatement.close();
            ConnectionPool.RecycleConnection(connection);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
