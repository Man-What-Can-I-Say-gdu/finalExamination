package com.pumpkin.Service.ServiceImp;

import com.pumpkin.Dao.DaoImp.ChessStyleImp;
import com.pumpkin.Service.chessStyleService;
import com.pumpkin.entity.Chess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ChessStyleServiceImp implements chessStyleService {
    ChessStyleImp chessStyleImp = new ChessStyleImp();

    @Override
    public boolean buildChessStyle(String chessStyleId) {
        return chessStyleImp.buildChessBoard(chessStyleId);
    }

    @Override
    public boolean insertChess(String chessStyleId, Chess chess) {
        if(chessStyleImp.insertChessPosition(chessStyleId,chess.getPosition(),chess.getSteps())){
           chess = findSurroundingChess(chessStyleId,chess);
           chessStyleImp.updateChessSurrounding(chessStyleId,chess);
           return true;
        }
        return false;
    }

    @Override
    public boolean moveChess(String chessStyleId, int steps) {
        return chessStyleImp.removeChessPosition(chessStyleId,steps);
    }

    @Override
    public ArrayList<Chess> getChessStyles(String chessStyleId) {
        return chessStyleImp.selectChessStyle(chessStyleId);
    }

    @Override
    public boolean findYIsSuccess(String chessStyleId, Chess chess) {
        int number = circulateFind(0,chessStyleId,chess,"front");
        number += circulateFind(0,chessStyleId,chess,"rear");
        return number >=5;
    }


    @Override
    public boolean findXIsSuccess(String chessStyleId, Chess chess) {
        int number = circulateFind(0,chessStyleId,chess,"theLeft");
        number += circulateFind(0,chessStyleId,chess,"theRight");
        return number >=5;
    }

    @Override
    public boolean findDiagonalIsSuccess(String chessStyleId, Chess chess) {
        int number = circulateFind(0,chessStyleId,chess,"frontLeft");
        number += circulateFind(0,chessStyleId,chess,"rearRight");
        return number >=5;
    }

    @Override
    public boolean findAntiDiagonalIsSuccess(String chessStyleId, Chess chess) {
        int number = circulateFind(0,chessStyleId,chess,"frontRight");
        number += circulateFind(0,chessStyleId,chess,"rearLeft");
        return number >=5;
    }

    /**
     * 递归查找同个方向上同色棋子的数量
     * @param number
     * @param chessStyleId
     * @param chess
     * @param direction
     * @return
     */
    public int circulateFind(int number, String chessStyleId, Chess chess,String direction) {
        int[] positionArray = new int[2];
        String[] splitPosition = chess.getPosition().split(",");
        //获取x
        positionArray[0] = splitPosition[0].toCharArray()[1];
        //获取y
        positionArray[1] = splitPosition[1].toCharArray()[0];
        chess = chessStyleImp.selectChessBySpecialPosition(direction,chessStyleId,positionArray[0],positionArray[1]);
        //判断是否为靠边的棋子
        if(chess == null){
            return number;
        }
        try {
            Method method = Chess.class.getMethod("is"+direction,boolean.class);
            //当下一个棋子不为同色棋子或者查找不到当前棋子时
            if((boolean)method.invoke(chess)|| chessStyleImp.selectChessBySpecialPosition(direction,chessStyleId,positionArray[0],positionArray[1]) == null){
                return number;
            }else{
                //更新棋子的位置信息
                chess.setPosition(chessStyleImp.getPositionString(direction,positionArray[0],positionArray[1]));
                return circulateFind(number+1,chessStyleId,chess,direction);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 查找周围是否存在同色棋子
     */
    public Chess findSurroundingChess(String chessStyleId, Chess chess) {
        //添加成功，在周围查找是否含有同色棋子
        int[] positionArray = new int[2];
        String[] splitPosition = chess.getPosition().split(",");
        //获取x
        positionArray[0] = splitPosition[0].toCharArray()[1];
        //获取y
        positionArray[1] = splitPosition[1].toCharArray()[0];
        chess.setForward(chessStyleImp.selectChessBySpecialPosition("forward",chessStyleId,positionArray[0],positionArray[1]).getSteps()%2 == chess.getSteps()%2);
        chess.setForward(chessStyleImp.selectChessBySpecialPosition("rear",chessStyleId,positionArray[0],positionArray[1]).getSteps()%2 == chess.getSteps()%2);
        chess.setForward(chessStyleImp.selectChessBySpecialPosition("theLeft",chessStyleId,positionArray[0],positionArray[1]).getSteps()%2 == chess.getSteps()%2);
        chess.setForward(chessStyleImp.selectChessBySpecialPosition("theRight",chessStyleId,positionArray[0],positionArray[1]).getSteps()%2 == chess.getSteps()%2);
        chess.setForward(chessStyleImp.selectChessBySpecialPosition("leftFront",chessStyleId,positionArray[0],positionArray[1]).getSteps()%2 == chess.getSteps()%2);
        chess.setForward(chessStyleImp.selectChessBySpecialPosition("rightFront",chessStyleId,positionArray[0],positionArray[1]).getSteps()%2 == chess.getSteps()%2);
        chess.setForward(chessStyleImp.selectChessBySpecialPosition("leftRear",chessStyleId,positionArray[0],positionArray[1]).getSteps()%2 == chess.getSteps()%2);
        chess.setForward(chessStyleImp.selectChessBySpecialPosition("rightRear",chessStyleId,positionArray[0],positionArray[1]).getSteps()%2 == chess.getSteps()%2);
        return chess;
    }


}
