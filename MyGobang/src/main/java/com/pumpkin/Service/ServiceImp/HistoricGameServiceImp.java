package com.pumpkin.Service.ServiceImp;

import com.pumpkin.Dao.DaoImp.HistoricGameImp;
import com.pumpkin.Dao.HistoricGameDao;
import com.pumpkin.Service.HistoricGameService;
import com.pumpkin.entity.HistoricGame;

import java.util.ArrayList;

public class HistoricGameServiceImp implements HistoricGameService {
    HistoricGameImp historicGameImp = new HistoricGameImp();
    @Override
    public ArrayList<HistoricGame> selectHistoricGame(HistoricGame historicGame) {
        return historicGameImp.selectGameById(historicGame);
    }

    @Override
    public boolean insertHistoricGame(HistoricGame historicGame) {
        return historicGameImp.saveGame(historicGame);
    }
}
