package com.Pumpkin.Dao;

import com.Pumpkin.entity.Chess;

import java.util.ArrayList;

public interface ChessBoardStyleDao {
    /**
     * 创建存放对局棋子位置的表
     * @param GameId 对局id
     */
    public void buildChessBoardStyle(String GameId);
    /**
     * 保存最终棋盘样式
     * @param chess 最终棋盘的所有棋子
     */
    public void insertChessIntoBoard(String GameId,ArrayList<Chess> chess);

    /**
     * 根据对局id查找相应棋局的最终样式
     */
    public ArrayList<Chess> selectFianlBoardByGameId(String gameId);
}
