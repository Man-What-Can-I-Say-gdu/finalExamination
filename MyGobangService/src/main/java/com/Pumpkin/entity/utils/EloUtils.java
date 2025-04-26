package com.Pumpkin.entity.utils;

import java.math.BigDecimal;

public class EloUtils {
    private final static BigDecimal DONE= new BigDecimal("1.0");
    private final static BigDecimal D400 = new BigDecimal("400");



    /*
     * 通过ELO算法计算比赛得分
     * ra        玩家A本轮比赛前得分
     * rb        玩家B本轮比赛钱得分
     * sa        A VS B 结果：胜利 1，平 0.5，失败 0  sa代表A玩家胜利或失败结果，统一由分数高的一方计算出积分变动的情况，若分数相同选择其中一方计算
     * k         极限值，代表理论上最多可以赢的分数和失去的分数
     * Limit     是否开启下限为0的限制
     * ea        玩家a获胜的期望概率
     * return    比赛结束后返回A,B玩家的得分和失分
     */
    public static ELOResult rating(int ra, int rb, float sa, int k, boolean limit){
        if (ra > rb) {
            ELOResult result = rating(rb, ra, 1.0f - sa, k, limit);
            int temp = result.getRa();
            result.setRa(result.getRb());
            result.setRb(temp);
            return result;
        }

        BigDecimal ea = DONE.divide(DONE.add(new BigDecimal(Math.pow(10, new BigDecimal(rb - ra).divide(D400, 6, BigDecimal.ROUND_HALF_UP).doubleValue()))), 6, BigDecimal.ROUND_HALF_UP);
        double score = new BigDecimal(k).multiply(new BigDecimal(sa).subtract(ea)).doubleValue();

        //为正数变动积分向上取证
        int scoreI = (int) Math.ceil(score);
        if (score < 0d) { //为负数变动积分向下取证
            scoreI = (int) Math.floor(score);
        }

        ELOResult elo = new ELOResult();
        elo.setRa((ra + scoreI < 0 && limit) ? 0 : ra + scoreI);
        elo.setRb((rb - scoreI < 0 && limit) ? 0 : rb - scoreI);


        elo.setEa(ea);
        elo.setEb(DONE.subtract(ea));

        return elo;
    }
}
