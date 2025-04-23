package com.pumpkin.controller;

import com.pumpkin.entity.Chess;

import java.util.ArrayList;

public interface ChessStyleController {
    /**
     * 创建棋盘数据表
     */
    public void buildChessStyle(String chessStyleId);

    /**
     * 添加棋子数据
     */
    public boolean insertChess(String chessStyleId, Chess chess);

    /**
     * 获取最终棋盘数据
     */
    public ArrayList<Chess> getFinalChessStyle(String chessStyleId);

    /**
     * 悔棋
     */
    public void Repentance(String chessStyleId,Chess chess,boolean myType);



}
