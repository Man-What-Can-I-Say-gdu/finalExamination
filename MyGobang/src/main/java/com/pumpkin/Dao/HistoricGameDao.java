package com.pumpkin.Dao;

import com.pumpkin.entity.HistoricGame;

import java.util.ArrayList;

public interface HistoricGameDao {
    /**
     * 保存对局
     */
    public boolean saveGame(HistoricGame game);

    /**
     * 查找对局
     */
    public HistoricGame selectGame(HistoricGame game);

    /**
     * 通过id查找对局
     */
    public ArrayList<HistoricGame> selectGameById(HistoricGame game);
}
