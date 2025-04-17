package com.pumpkin.Dao;

import com.pumpkin.entity.Gamer;

public interface GamerMapper{
    /**
     * 关联user表，在user创建时同步创建gamer
     */
    public boolean insertGamer(int id);

    /**
     * 通过id获取Gamer的所有信息
     */
    public Gamer selectAllFromUser(int id);

    /**
     * 在对局完成后更新对局数据
     */
    public boolean updateGamerMatchData(Gamer gamer);

    /**
     * 在赛季结束后对数据赛季数据进行清空
     */
    public boolean deleteGamerSeasonData(int id);

    /**
     * 更新段位数据
     */
    public boolean updateGamerDan(Gamer gamer);
}
