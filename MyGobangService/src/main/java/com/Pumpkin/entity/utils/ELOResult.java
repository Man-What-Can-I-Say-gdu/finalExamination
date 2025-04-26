package com.Pumpkin.entity.utils;

import java.math.BigDecimal;

public class ELOResult {
    /**
     * 玩家A本轮比赛后得分
     */
    private int ra;
    /**
     * 玩家b本轮比赛后得分
     */
    private int rb;

    /**
     * a获胜的概率
     */
    private BigDecimal ea;

    /**
     * b获胜的概率
     */
    private BigDecimal eb;

    public BigDecimal getEa() {
        return ea;
    }

    public void setEa(BigDecimal ea) {
        this.ea = ea;
    }

    public BigDecimal getEb() {
        return eb;
    }

    public void setEb(BigDecimal eb) {
        this.eb = eb;
    }

    public int getRa() {
        return ra;
    }

    public void setRa(int ra) {
        this.ra = ra;
    }

    public int getRb() {
        return rb;
    }

    public void setRb(int rb) {
        this.rb = rb;
    }
}
