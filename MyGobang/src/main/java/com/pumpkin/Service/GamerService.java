package com.pumpkin.Service;

import com.pumpkin.entity.Gamer;

public interface GamerService {

    /**
     * 获取gamer的值用于展示gamer
     */
    public Gamer getGamer(int id);

    /**
     * 修改段位
     */
    public boolean updateGamerDan(Gamer gamer);

    /**
     * 修改玩家战绩数据
     */
    public boolean updateGamerMatchData(Gamer gamer);

    /**
     * 清空赛季游戏数据
     */
    public boolean clearSeasonMatchData(int id);

    /**
     * 创建用户时同步创建玩家数据
     */
    public boolean insertGamer(int id);


    Gamer selectGamerById(int id);
}
