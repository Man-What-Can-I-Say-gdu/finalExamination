package com.pumpkin.Service.ServiceImp;

import com.pumpkin.Dao.DaoImp.ChessStyleImp;
import com.pumpkin.Service.chessStyleService;
import com.pumpkin.entity.Chess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChessStyleServiceImp implements chessStyleService {
    ChessStyleImp chessStyleImp = new ChessStyleImp();

    @Override
    public boolean buildChessStyle(String chessStyleId) {
        return chessStyleImp.buildChessBoard(chessStyleId);
    }

    @Override
    public boolean insertChess(String chessStyleId, Chess chess) {
        if(chessStyleImp.insertChessPosition(chessStyleId,chess.getPosition(),chess.getSteps(),chess.isType())){
            chess = findSurroundingChess(chessStyleId,chess);
            chessStyleImp.updateChessSurrounding(chessStyleId,chess);
            return true;
        }
        return false;
    }

    @Override
    public boolean moveChess(String chessStyleId, Chess chess,boolean myType) {
        return chessStyleImp.removeChessPosition(chessStyleId,chess,myType);
    }

    @Override
    public ArrayList<Chess> getChessStyles(String chessStyleId) {
        return chessStyleImp.selectChessStyle(chessStyleId);
    }

    /**
     * 查找y方向方向上是否存在一定数量同色棋子
     * @param chessStyleId
     * @param chess
     * @return
     */
    @Override
    public Map<String,Object> findYIsSuccess(String chessStyleId, Chess chess) {
        return findSpecialDirectionIsExistConfirmNumbsChess("forward","rear",chessStyleId, chess);
    }
    /**
     * 查找x方向上是否存在一定数量同色棋子
     * @param chessStyleId
     * @param chess
     * @return
     */
    @Override
    public Map<String,Object> findXIsSuccess(String chessStyleId, Chess chess) {
        return findSpecialDirectionIsExistConfirmNumbsChess("theLeft","theRight",chessStyleId, chess);
    }

    /**
     * 查找正对角线方向上是否存在一定数量同色棋子
     * @param chessStyleId
     * @param chess
     * @return
     */
    @Override
    public Map<String,Object> findDiagonalIsSuccess(String chessStyleId, Chess chess) {
        return findSpecialDirectionIsExistConfirmNumbsChess("frontLeft","rearRight",chessStyleId, chess);
    }

    /**
     * 查找反对角线方向上是否存在一定数量同色棋子
     * @param chessStyleId
     * @param chess
     * @return
     */
    @Override
    public Map<String,Object> findAntiDiagonalIsSuccess(String chessStyleId, Chess chess) {
        return findSpecialDirectionIsExistConfirmNumbsChess("frontRight","rearLeft",chessStyleId, chess);

    }

    /**
     * 查找特定线路上是否存在一定数量同色棋子
     * @param direction
     * @param AntiDirection
     * @param chessStyleId
     * @param chess
     * @return
     */
    private Map<String, Object> findSpecialDirectionIsExistConfirmNumbsChess(String direction,String AntiDirection,String chessStyleId, Chess chess) {
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> tmpMap = new HashMap<>();
        tmpMap = circulateFind(0, chessStyleId, chess,"frontLeft");
        map.put("number",0);
        map.put("isExist",false);
        map.put("secondSideExistChess",false);
        map.replace("number",(int)map.get("number")+(int)tmpMap.get("number"));
        if((boolean)map.get("isExist")){
            //判断是否棋子末端是否存在空位，若存在再判断末端向后是否有第二个棋子
            map.replace("isExist",tmpMap.get("isExist"));
            map.replace("secondSideExistChess",tmpMap.get("secondSideExistChess"));
        }
        //向反方向进行查找
        tmpMap = circulateFind(0, chessStyleId, chess,"frontRight");
        map.replace("number",(int)map.get("number")+(int)tmpMap.get("number"));
        if((boolean)map.get("isExist")){
            //已经存在一路活棋，则进行替换
            map.replace("isExist",tmpMap.get("isExist"));
            //判断是否存在第二个空闲位置，如果不存在则赋值为假
            map.replace("secondSideExistChess",((boolean)map.get("secondSideExistChess"))?true:tmpMap.get("secondSideExistChess"));
        }
        return map;
    }
    /**
     * 递归查找同个方向上同色棋子的数量
     * @param number
     * @param chessStyleId
     * @param chess
     * @param direction
     * @return
     */
    public Map<String,Object> circulateFind(int number, String chessStyleId, Chess chess,String direction) {
        //存放棋子计数的结果,包括末端是否有其他不同花色的棋子，最多有多少棋子(如果棋子在棋盘边缘，则假设棋子在靠棋盘边缘处有不同花色的棋子)
        Map<String,Object> result = new HashMap<>();
        int[] positionArray = new int[2];
        String[] splitPosition = chess.getPosition().split(",");
        //获取x
        positionArray[0] = splitPosition[0].toCharArray()[1];
        //获取y
        positionArray[1] = splitPosition[1].toCharArray()[0];
        //获取特定位置的棋子的数据
        chess = chessStyleImp.selectChessBySpecialPosition(direction,chessStyleId,positionArray[0],positionArray[1]);
        //判断是否为靠边的棋子
        if(chess == null){
            result.put("isExist",true);
            result.put("number",number);
            return result;
        }
        //判断棋子是否存在，不存在则再查找是否还有空位置（为了确认三三禁手）返回
        if(!chess.isExist()){
            result.put("isExist",false);
            result.put("number",number);
            chess = chessStyleImp.selectChessBySpecialPosition(direction,chessStyleId,positionArray[0],positionArray[1]);
            //再次判断是否有空位，记录在map集合中
            result.put("secondSideExistChess",chess.isExist());
            return result;
        }
        try {
            Method method = Chess.class.getMethod("is"+direction,boolean.class);
            //当下一个棋子不为同色棋子
            if((boolean)method.invoke(chess)){
                result.put("isExist",true);
                result.put("number",number);
                return result;
            }else{
                //更新棋子的位置信息
                chess.setPosition(chessStyleImp.getPositionString(direction,positionArray[0],positionArray[1]));
                number++;
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
