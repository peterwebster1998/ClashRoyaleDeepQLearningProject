/*
Author: Peter Webster
Date: 5/12/20

5/18/2020 UPDATE: Added damage potential for implementation with TeamLL
 */

import java.awt.*;

public class Building extends Card{
    private int HP, currentHP, lifeSpan;
    private double dmgPotential;

    public Building(int teamNo, int cost, int ID, double xPos, double yPos, long currentTime, double width, double height,
                    int HP, int lifeSpan){
        super(teamNo, cost, ID, xPos, yPos, currentTime, width, height);
        this.HP = HP;
        this.currentHP = HP;
        this.lifeSpan = lifeSpan;
    }

    public int getHP() {
        return HP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public long getLifeSpan() {
        return lifeSpan;
    }

    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }

    public double getDmgPotential() {
        this.dmgPotential = this.getCost() * this.getID();
        return dmgPotential;
    }

    public void draw(Color teamColor, int teamDrawReq){
        StdDraw.setPenColor(teamColor);
        if(teamDrawReq == 1) {
            StdDraw.filledRectangle(this.getPos()[0], this.getPos()[1], this.getWidth() / 2, this.getHeight() / 2);
        } else {
            StdDraw.filledRectangle(GameBoard.getXdim()-this.getPos()[0], GameBoard.getYdim()-this.getPos()[1], this.getWidth() / 2, this.getHeight() / 2);
        }
        drawHealthbar(teamDrawReq);
    }

    private void drawHealthbar(int teamDrawReq){
        StdDraw.setPenColor(StdDraw.GREEN);
        if(teamDrawReq == 1) {
            StdDraw.filledRectangle(this.getPos()[0], this.getPos()[1] + (1.25 * (this.getHeight() / 2)), this.getWidth() / 2, 0.1);
            StdDraw.setPenColor(StdDraw.RED);
            double healthLost = (double) (this.getHP() - this.getCurrentHP()) / (double) this.getHP();
            double halfWidth = healthLost * this.getWidth() / 2;
            double x = this.getPos()[0] + this.getWidth() / 2 - halfWidth;
            StdDraw.filledRectangle(x, this.getPos()[1] + (1.25 * (this.getHeight() / 2)), halfWidth, 0.1);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(this.getPos()[0], this.getPos()[1] + (1.25*(this.getHeight()/2)), String.valueOf(this.getCurrentHP()));
        } else {
            StdDraw.filledRectangle(GameBoard.getXdim()-this.getPos()[0], (GameBoard.getYdim()-this.getPos()[1]) + (1.25 * (this.getHeight() / 2)), this.getWidth() / 2, 0.1);
            StdDraw.setPenColor(StdDraw.RED);
            double healthLost = (double) (this.getHP() - this.getCurrentHP()) / (double) this.getHP();
            double halfWidth = healthLost * this.getWidth() / 2;
            double x = GameBoard.getXdim() - (this.getPos()[0] + this.getWidth() / 2 - halfWidth);
            StdDraw.filledRectangle(x, (GameBoard.getYdim()-this.getPos()[1]) + (1.25 * (this.getHeight() / 2)), halfWidth, 0.1);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(GameBoard.getXdim()-this.getPos()[0], (GameBoard.getYdim()-this.getPos()[1]) + (1.25*(this.getHeight()/2)), String.valueOf(this.getCurrentHP()));
        }
    }

    //To be overridden
    public void update(GameStructures towers, TeamLL enemy){
        this.updateLife();
    }

    public void updateLife(){
        long currentTime = System.currentTimeMillis();
        int newHP = this.getCurrentHP() - (int)(this.getHP()*(currentTime-getUpdateTS())/(GameBoard.getSecs()*this.getLifeSpan()));
        this.setCurrentHP(newHP);
        this.setUpdateTS(currentTime);
    }

}
