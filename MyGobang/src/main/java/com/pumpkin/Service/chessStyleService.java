package com.pumpkin.Service;

import com.pumpkin.entity.Chess;

import java.util.ArrayList;
import java.util.Map;

public interface chessStyleService {
    /**
     * 创建表：第一步：查询表是否存在，第二步：为空则创建
     */
    public boolean buildChessStyle(String chessStyleId);

    /**
     * 添加棋子数据
     */
    public boolean insertChess(String chessStyleId, Chess chess);

    /**
     * 悔棋功能实现
     */
    public boolean moveChess(String chessStyleId, Chess chess,boolean myType);

    /**
     * 查看最终棋盘
     */
    public ArrayList<Chess> getChessStyles(String chessStyleId);


    /**
     * 查询y轴方位上是否有足够数量的棋子
     */
    public Map<String,Object> findYIsSuccess(String chessStyleId, Chess chess);
    /**
     * 查询x轴方位上是否有足够数量的棋子
     */
    public Map<String,Object> findXIsSuccess(String chessStyleId,Chess chess);

    /**
     * 查询正对角线方位上是否有足够数量的棋子
     */
    public Map<String,Object> findDiagonalIsSuccess(String chessStyleId,Chess chess);

    /**
     * 查询反对角线上是否有足够数量的棋子
     */
    public Map<String,Object> findAntiDiagonalIsSuccess(String chessStyleId,Chess chess);
}
