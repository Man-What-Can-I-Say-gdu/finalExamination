package com.pumpkin.controller.ControllerImp;

import com.pumpkin.Service.ServiceImp.ChessStyleServiceImp;
import com.pumpkin.controller.ChessStyleController;
import com.pumpkin.entity.Chess;

import java.util.ArrayList;
import java.util.Map;

public class ChessStyleControllerImp implements ChessStyleController {
    ChessStyleServiceImp chessStyleServiceImp = new ChessStyleServiceImp();

    @Override
    public void buildChessStyle(String chessStyleId) {
        chessStyleServiceImp.buildChessStyle(chessStyleId);
    }

    @Override
    public  boolean insertChess(String chessStyleId, Chess chess) {
        return chessStyleServiceImp.insertChess(chessStyleId, chess);
    }

    @Override
    public ArrayList<Chess> getFinalChessStyle(String chessStyleId) {
        return chessStyleServiceImp.getChessStyles(chessStyleId);
    }

    @Override
    public void Repentance(String chessStyleId,Chess chess,boolean myType) {
        chessStyleServiceImp.moveChess(chessStyleId,chess,myType);
    }

    /**
     * 判断是否正确
     * @param chessStyleId
     * @param chess
     * @return winCondition:1为无影响，2为胜利，0为失败
     */
    public int isWin(String chessStyleId,Chess chess){
        //判断禁手的数量
        int thirdForbidNumb = 0;
        int forthForbidNumb = 0;
        int longForbidNumb = 0;
        int fiveNumb = 0;
        int winCondition = 1;
        Map<String,Object> map = chessStyleServiceImp.findXIsSuccess(chessStyleId,chess);
        if((boolean) map.get("isExist")) {
            //存在活棋
            switch((int)map.get("number")){
                case 1:
                case 2:
                case 5:{
                    break;
                } case 3: {
                    map.get("secondSideExistChess");
                    thirdForbidNumb++;
                    break;
                } case 4: {
                    forthForbidNumb++;
                    break;
                }default:{
                    longForbidNumb++;
                }
            }
        }else{
            //出现一个四连则不管是否有空位都计数
            if((int)map.get("number") == 4){
                forthForbidNumb++;
            }
            fiveNumb += (int)map.get("number")==5 ? 1 : 0;
        }
        map = chessStyleServiceImp.findYIsSuccess(chessStyleId,chess);
        if((boolean) map.get("isExist")) {
            //存在活棋
            switch((int)map.get("number")){
                case 1:
                case 2:
                case 5:{
                    break;
                } case 3: {
                    map.get("secondSideExistChess");
                    thirdForbidNumb++;
                    break;
                } case 4: {
                    forthForbidNumb++;
                    break;
                }default:{
                    longForbidNumb++;
                }
            }
        }else{
            //出现一个四连则不管是否有空位都计数
            if((int)map.get("number") == 4){
                forthForbidNumb++;
            }
            fiveNumb += (int)map.get("number")==5 ? 1 : 0;
        }
        map = chessStyleServiceImp.findDiagonalIsSuccess(chessStyleId,chess);
        if((boolean) map.get("isExist")) {
            //存在活棋
            switch((int)map.get("number")){
                case 1:
                case 2:
                case 5:{
                    break;
                } case 3: {
                    map.get("secondSideExistChess");
                    thirdForbidNumb++;
                    break;
                } case 4: {
                    forthForbidNumb++;
                    break;
                }default:{
                    longForbidNumb++;
                }
            }
        }else{
            //出现一个四连则不管是否有空位都计数
            if((int)map.get("number") == 4){
                forthForbidNumb++;
            }
            fiveNumb += (int)map.get("number")==5 ? 1 : 0;
        }
        map = chessStyleServiceImp.findAntiDiagonalIsSuccess(chessStyleId,chess);
        if((boolean) map.get("isExist")) {
            //存在活棋
            switch((int)map.get("number")){
                case 1:
                case 2:
                case 5:{
                    break;
                } case 3: {
                    map.get("secondSideExistChess");
                    thirdForbidNumb++;
                    break;
                } case 4: {
                    forthForbidNumb++;
                    break;
                }default:{
                    longForbidNumb++;
                }
            }
        }else{
            //出现一个四连则不管是否有空位都计数
            if((int)map.get("number") == 4){
                forthForbidNumb++;
            }
            fiveNumb += (int)map.get("number")==5 ? 1 : 0;
        }
        //获取禁手数量后比对一下存在的并返回结局详情
        //判断棋子类型和禁手情况
        if((thirdForbidNumb >= 2 || forthForbidNumb >= 2 || longForbidNumb > 0) && chess.isType()){
            //条件错误则判负
            winCondition = 0;
        }else{
            if(chess.isType()){
                //黑棋获胜的条件
                winCondition = fiveNumb>0 ? 2 : 1;
            }else{
                winCondition = (fiveNumb>0||longForbidNumb>0) ? 1 : 0;
            }
        }
        return winCondition;
    }


}
