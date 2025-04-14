package com.pumpkin.entity;

public class Gamer extends User{
    /**
     * 段位:由于五子棋段位共九段，因此游戏中也采用九段
     */
    private int dan;
    /**
     * 胜率
     */
    private double winRate;
    /**
     * 总游玩场次
     */
    private int sumPlayNumb;
    /**
     * 赛季游玩场次
     */
    private int seasonPlayNumb;
    /**
     * 总胜场
     */
    private int sumWinNumb;
    /**
     * 赛季胜场
     */
    private int seasonWinNumb;

    public int getDan() {
        return dan;
    }

    public void setDan(int dan) {
        this.dan = dan;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public int getSumPlayNumb() {
        return sumPlayNumb;
    }

    public void setSumPlayNumb(int sumPlayNumb) {
        this.sumPlayNumb = sumPlayNumb;
    }

    public int getSeasonPlayNumb() {
        return seasonPlayNumb;
    }

    public void setSeasonPlayNumb(int seasonPlayNumb) {
        this.seasonPlayNumb = seasonPlayNumb;
    }

    public int getSumWinNumb() {
        return sumWinNumb;
    }

    public void setSumWinNumb(int sumWinNumb) {
        this.sumWinNumb = sumWinNumb;
    }

    public int getSeasonWinNumb() {
        return seasonWinNumb;
    }

    public void setSeasonWinNumb(int seasonWinNumb) {
        this.seasonWinNumb = seasonWinNumb;
    }

    public Gamer(int dan, double winRate, int sumPlayNumb, int seasonPlayNumb, int sumWinNumb, int seasonWinNumb) {
        this.dan = dan;
        this.winRate = winRate;
        this.sumPlayNumb = sumPlayNumb;
        this.seasonPlayNumb = seasonPlayNumb;
        this.sumWinNumb = sumWinNumb;
        this.seasonWinNumb = seasonWinNumb;
    }

    public Gamer(int id, String name, String password, String email, String phoneNumber, int dan, double winRate, int sumPlayNumb, int seasonPlayNumb, int sumWinNumb, int seasonWinNumb) {
        super(id, name, password, email, phoneNumber);
        this.dan = dan;
        this.winRate = winRate;
        this.sumPlayNumb = sumPlayNumb;
        this.seasonPlayNumb = seasonPlayNumb;
        this.sumWinNumb = sumWinNumb;
        this.seasonWinNumb = seasonWinNumb;
    }
}
