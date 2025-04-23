package com.Pumpkin.Service.ServiceImp;

import com.Pumpkin.Dao.DaoImp.HistoricGameDaoImp;
import com.Pumpkin.Dao.HistoricGameDao;
import com.Pumpkin.Service.GameService;
import com.Pumpkin.entity.HistoricGame;

import java.util.ArrayList;

public class GameServiceImp implements GameService {
    HistoricGameDao historicGameDao = new HistoricGameDaoImp();
    @Override
    public boolean buildGame(HistoricGame historicGame) {
        return historicGameDao.buildGame(historicGame);
    }

    @Override
    public boolean saveGame(HistoricGame historicGame) {
        return historicGameDao.saveGame(historicGame);
    }

    @Override
    public HistoricGame findSpecialIdGame(HistoricGame historicGame) {
        return historicGameDao.selectGame(historicGame);
    }

    @Override
    public ArrayList<HistoricGame> findAllGamesByGamerId(int gamerId) {
        return historicGameDao.selectAllGameById(gamerId);
    }

    @Override
    public ArrayList<HistoricGame> findAllGamesBySpecialTime(String specialTime) {
        return historicGameDao.selectAllGameInSpecialTIme(specialTime);
    }
}