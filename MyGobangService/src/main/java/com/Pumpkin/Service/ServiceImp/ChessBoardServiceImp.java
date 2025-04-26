package com.Pumpkin.Service.ServiceImp;

import com.Pumpkin.Dao.ChessBoardStyleDao;
import com.Pumpkin.Dao.DaoImp.ChessBoardStyleDaoImp;
import com.Pumpkin.Service.ChessBoardService;
import com.Pumpkin.entity.Chess;

import java.util.ArrayList;

public class ChessBoardServiceImp implements ChessBoardService {
    ChessBoardStyleDao chessBoardStyleDao = new ChessBoardStyleDaoImp();
    @Override
    public void buildChessBoard(String GameId) {
        chessBoardStyleDao.buildChessBoardStyle(GameId);
    }

    @Override
    public void printChessBoard(String GameId, ArrayList<Chess> chess) {
        chessBoardStyleDao.insertChessIntoBoard(GameId,chess);
    }

    @Override
    public ArrayList<Chess> getChessBoard(String GameId) {
        return chessBoardStyleDao.selectFianlBoardByGameId(GameId);
    }
}
