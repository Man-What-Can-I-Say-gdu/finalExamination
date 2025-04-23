package com.pumpkin.Service;

import com.pumpkin.entity.HistoricGame;

import java.util.ArrayList;

public interface HistoricGameService {
    /**
     * 查找历史对局
     */
    public ArrayList<HistoricGame> selectHistoricGame(HistoricGame historicGame);

    /**
     * 保存对局
     */
    public boolean insertHistoricGame(HistoricGame historicGame);
}
