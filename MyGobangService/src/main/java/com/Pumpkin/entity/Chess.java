package com.Pumpkin.entity;

public class Chess {
    /**
     * 棋子的位置,格式（x，y）
     */
    private String position;
    /**
     * 棋子步数：steps，棋子的类型由steps%2得到
     */
    private int steps;

    /**
     * 棋子花色:真为黑，假为白
     */
    private boolean type;

    public boolean isStyle() {
        return type;
    }

    public void setStyle(boolean style) {
        this.type = style;
    }

    public Chess(String position, int steps, boolean type) {
        this.position = position;
        this.steps = steps;
        this.type = type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public Chess() {
    }


}

