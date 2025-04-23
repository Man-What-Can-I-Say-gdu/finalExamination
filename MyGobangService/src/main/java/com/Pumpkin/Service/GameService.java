package com.Pumpkin.Service;

import com.Pumpkin.Dao.HistoricGameDao;
import com.Pumpkin.entity.HistoricGame;

import java.util.ArrayList;

public interface GameService {
    /**
     * 创建游戏对局
     */
    public boolean buildGame(HistoricGame historicGame);

    /**
     * 保存游戏对局
     */
    public boolean saveGame(HistoricGame historicGame);

    /**
     * 查找游戏指定id对局
     */
    public HistoricGame findSpecialIdGame(HistoricGame historicGame);

    /**
     * 查找指定用户的所有游戏对局
     */
    public ArrayList<HistoricGame> findAllGamesByGamerId(int GamerId);

    /**
     * 查找指定时间的所有对局
     */
    public ArrayList<HistoricGame> findAllGamesBySpecialTime(String GameName);
}
