package com.pumpkin.controller;

import com.pumpkin.entity.Gamer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface GamerController {

    /**
     * 展示Gamer的所有数据
     */
    public void showGamerInfo(HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新战绩数据
     */
    public void updateMatchData(HttpServletRequest request, HttpServletResponse response);

    /**
     * 更新段位
     */
    public boolean updateDanInfo(HttpServletResponse response, Gamer gamer);

    /**
     * 清空赛季段位数据
     */
    public void clearSeasonData(HttpServletRequest request, HttpServletResponse response);
}
