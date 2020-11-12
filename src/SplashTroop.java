/*
Author: Peter Webster
Date: 5/12/20
 */

public class SplashTroop extends Troop{

    private double splashRad;

    public SplashTroop(int teamNo, int cost, int ID, double xPos, double yPos, long currentTime, double width, double height,
                       int HP, int damage, double moveSpeed, double attackSpeed, double attackRange, double sightRange, boolean flying,
                       double splashRad){
        super(teamNo, cost, ID, xPos, yPos, currentTime, width, height,
                HP, damage, moveSpeed, attackSpeed, attackRange, sightRange, flying);
        this.splashRad = splashRad;
    }

    public double getSplashRad() {
        return splashRad;
    }
}
