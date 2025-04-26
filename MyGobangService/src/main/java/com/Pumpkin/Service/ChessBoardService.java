package com.Pumpkin.Service;

import com.Pumpkin.entity.Chess;

import java.util.ArrayList;

public interface ChessBoardService {
    /**
     * 创建标识棋盘的表
     */
    public void buildChessBoard(String GameId);
    /**
     * 保存最终棋盘的样式
     */
    public void printChessBoard(String GameId, ArrayList<Chess> chess);
    /**
     * 根据对局id查找相应的最终棋盘样式
     */
    public ArrayList<Chess> getChessBoard(String GameId);


}
