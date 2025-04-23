package com.Pumpkin.Dao;

import com.Pumpkin.entity.HistoricGame;

import java.util.ArrayList;

public interface HistoricGameDao {
    /**
     * 添加游戏数据
     */
    public boolean buildGame(HistoricGame historicGame);

    /**
     * 对局结束后保存游戏数据
     */
    public boolean saveGame(HistoricGame historicGame);

    /**
     * 查找指定id的游戏对局
     */
    public HistoricGame selectGame(HistoricGame historicGame);

    /**
     * 查找指定用户id的所有游戏对局
     */
    public ArrayList<HistoricGame> selectAllGameById(int id);

    /**
     * 模糊查找某个时间段创建的所有对局
     */
    public ArrayList<HistoricGame> selectAllGameInSpecialTIme(String specialTIme);
}
