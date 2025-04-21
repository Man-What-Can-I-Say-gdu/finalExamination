package com.Pumpkin.entity;

public class Gamer extends User{

    /**
     * id
     */
    private int id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

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

    /**
     * 段位积分
     */
    private int points;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Gamer(int dan, int points, int seasonPlayNumb, int seasonWinNumb, int sumPlayNumb, int sumWinNumb, double winRate) {
        this.dan = dan;
        this.points = points;
        this.seasonPlayNumb = seasonPlayNumb;
        this.seasonWinNumb = seasonWinNumb;
        this.sumPlayNumb = sumPlayNumb;
        this.sumWinNumb = sumWinNumb;
        this.winRate = winRate;
    }

    public Gamer(String email, int id, String name, String password, String phoneNumber, byte[] salt, int dan, int points, int seasonPlayNumb, int seasonWinNumb, int sumPlayNumb, int sumWinNumb, double winRate) {
        super(email, id, name, password, phoneNumber, salt);
        this.dan = dan;
        this.points = points;
        this.seasonPlayNumb = seasonPlayNumb;
        this.seasonWinNumb = seasonWinNumb;
        this.sumPlayNumb = sumPlayNumb;
        this.sumWinNumb = sumWinNumb;
        this.winRate = winRate;
    }

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
    public Gamer() {
    }

}

