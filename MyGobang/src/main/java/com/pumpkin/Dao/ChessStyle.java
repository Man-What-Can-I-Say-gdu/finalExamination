package com.pumpkin.Dao;


import com.pumpkin.entity.Chess;

import java.util.ArrayList;

public interface ChessStyle {
    /**
     * 创建棋盘表，用于记录单局的棋子位置
     */
    public boolean buildChessBoard(String chessStyleId);

    /**
     * 添加棋子位置
     */
    public boolean insertChessPosition(String chessStyleId, String position,int steps);

    /**
     * 悔棋功能：通过id（落子步数）进行回退,一次退回两颗
     */
    public boolean removeChessPosition(String chessStyleId,int steps);

    /**
     * 查找整个棋盘的数据
     */
public ArrayList<Chess> selectChessStyle(String chessStyleId);
}
