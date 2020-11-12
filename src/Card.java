/*
Author: Peter Webster
Date: 5/12/20

5/19/2020 UPDATE: added width & height for draw methods
5/24/2020 UPDATE: added teamNo instance variable
 */

import java.io.Serializable;

public class Card implements Serializable {

    private int cost, ID, teamNo;
    private double x, y, width, height;
    private long createTS, updateTS;    //Time stamps
    private String name;

    public Card(int teamNo, int cost, int ID, double xPos, double yPos, long currentTime, double width, double height){
        this.teamNo = teamNo;
        this.cost = cost;
        this.ID = ID;
        this.x = (teamNo == 1)?xPos:GameBoard.getXdim()-xPos;
        this.y = (teamNo == 1)?yPos:GameBoard.getYdim()-yPos;;
        this.width = width;
        this.height = height;
        this.createTS = currentTime;
        this.updateTS = currentTime;
    }

    public int getCost() {
        return cost;
    }

    public int getID() {
        return ID;
    }

    private double getX(){
        return x;
    }

    private double getY(){
        return y;
    }

    public double[] getPos(){
        double[] pos = new double[2];
        pos[0] = this.getX();
        pos[1] = this.getY();
        return pos;
    }

    public long getCreateTS() {
        return createTS;
    }

    public long getUpdateTS() {
        return updateTS;
    }

    public void setUpdateTS(long updateTS) {
        this.updateTS = updateTS;
    }

    public void setPos(double x, double y){
        this.x = x;
        this.y = y;
    }

    //Private helper method that calculated distance between given card and self
    public double distance(Card c){
        return Math.sqrt(Math.pow(c.getPos()[0] - this.getPos()[0], 2) + Math.pow(c.getPos()[1] - this.getPos()[1], 2));
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public int getTeamNo(){
        return teamNo;
    }

    public double[] AIstats(int player){
        double[] stats = new double[3];
        stats[0] = this.getID();
        stats[1] = (player==1)?this.getX():(GameBoard.getXdim()-this.getX());
        stats[2] = (player==1)?this.getY():(GameBoard.getYdim()-this.getY());
        return stats;
    }
}
