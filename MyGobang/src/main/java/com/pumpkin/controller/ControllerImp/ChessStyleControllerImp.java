package com.pumpkin.controller.ControllerImp;

import com.pumpkin.Service.ServiceImp.ChessStyleServiceImp;
import com.pumpkin.controller.ChessStyleController;
import com.pumpkin.entity.Chess;

import java.util.ArrayList;

public class ChessStyleControllerImp implements ChessStyleController {
    ChessStyleServiceImp chessStyleServiceImp = new ChessStyleServiceImp();

    @Override
    public void buildChessStyle(String chessStyleId) {
        chessStyleServiceImp.buildChessStyle(chessStyleId);
    }

    @Override
    public void insertChess(String chessStyleId, Chess chess) {
        chessStyleServiceImp.insertChess(chessStyleId, chess);
        isWin(chessStyleId,chess);
    }

    @Override
    public ArrayList<Chess> getFinalChessStyle(String chessStyleId) {
        return chessStyleServiceImp.getChessStyles(chessStyleId);
    }

    @Override
    public void Repentance(String chessStyleId,int steps) {
        chessStyleServiceImp.moveChess(chessStyleId,steps);
    }

    /**
     * 判断是否正确
     * @param chessStyleId
     * @param chess
     * @return
     */
    public boolean isWin(String chessStyleId,Chess chess){
        return chessStyleServiceImp.findXIsSuccess(chessStyleId, chess) || chessStyleServiceImp.findYIsSuccess(chessStyleId, chess) || chessStyleServiceImp.findDiagonalIsSuccess(chessStyleId, chess) || chessStyleServiceImp.findAntiDiagonalIsSuccess(chessStyleId, chess);
    }
}
