package com.pumpkin.entity;

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
     * y-1处是否存在同色棋
     */
    private boolean forward;
    /**
     * y+1是否存在同色棋
     */
    private boolean rear;

    /**
     * x-1是否存在同色棋
     */
    private boolean theLeft;

    /**
     * x+1是否存在同色棋
     */
    private boolean theRight;

    /**
     * x-1,y-1是否存在同色棋
     */
    private boolean leftFront;

    /**
     * x+1,y-1是否存在同色棋
     */
    private boolean rightFront;

    /**
     * x-1,y+1是否存在同色棋
     */
    private boolean leftRear;

    /**
     * x+1,y+1是否存在同色棋
     */
    private boolean rightRear;
    /**
     * 棋子连线
     */
    private char potential;

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public boolean isTheLeft() {
        return theLeft;
    }

    public void setTheLeft(boolean theLeft) {
        this.theLeft = theLeft;
    }

    public boolean isLeftFront() {
        return leftFront;
    }

    public void setLeftFront(boolean leftFront) {
        this.leftFront = leftFront;
    }

    public boolean isLeftRear() {
        return leftRear;
    }

    public void setLeftRear(boolean leftRear) {
        this.leftRear = leftRear;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public char getPotential() {
        return potential;
    }

    public void setPotential(char potential) {
        this.potential = potential;
    }

    public boolean isRear() {
        return rear;
    }

    public void setRear(boolean rear) {
        this.rear = rear;
    }

    public boolean isTheRight() {
        return theRight;
    }

    public void setTheRight(boolean theRight) {
        this.theRight = theRight;
    }

    public boolean isRightFront() {
        return rightFront;
    }

    public void setRightFront(boolean rightFront) {
        this.rightFront = rightFront;
    }

    public boolean isRightRear() {
        return rightRear;
    }

    public void setRightRear(boolean rightRear) {
        this.rightRear = rightRear;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public Chess() {
    }

    public Chess(boolean forward, boolean left, boolean leftFront, boolean leftRear, String position, char potential, boolean rear, boolean right, boolean rightFront, boolean rightRear, int steps) {
        this.forward = forward;
        this.theLeft = left;
        this.leftFront = leftFront;
        this.leftRear = leftRear;
        this.position = position;
        this.potential = potential;
        this.rear = rear;
        this.theRight = right;
        this.rightFront = rightFront;
        this.rightRear = rightRear;
        this.steps = steps;
    }
}
