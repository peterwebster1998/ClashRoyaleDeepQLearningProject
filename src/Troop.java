/*
Author: Peter Webster
Date: 5/12/20

5/18/2020 UPDATE: Added damage potential for implementation with TeamLL
5/22/2020 UPDATE: Started implementation of updatePos()
5/25/2020 UPDATE: Finished updatePos(), Added chase() & attack() helper methods & added update() method
 */

import java.awt.*;
import java.lang.Math;

public class Troop extends Card{

    private int HP, currentHP, damage;
    private double moveSpeed, attackSpeed, attackRange, sightRange, dmgPotential;
    private boolean flying;
    private long lastAttackTS;  //Timestamp
    private static final Color SHADOW_GREY = new Color(100, 100, 100, 150);
    private Troop targetUnit = null;
    private Building targetBuilding = null;

    public Troop(int teamNo, int cost, int ID, double xPos, double yPos, long currentTime, double width, double height,
                 int HP, int damage, double moveSpeed, double attackSpeed, double attackRange, double sightRange, boolean flying){
        super(teamNo, cost, ID, xPos, yPos, currentTime, width, height);
        this.HP = HP;
        this.currentHP = HP;
        this.damage = damage;
        this.moveSpeed = moveSpeed;
        this.attackSpeed = attackSpeed;
        this.attackRange = attackRange;
        this.sightRange = sightRange;
        this.flying = flying;
        this.lastAttackTS = currentTime;
    }

    public int getHP() {
        return HP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getDamage() {
        return damage;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getAttackRange() {
        return attackRange;
    }

    public double getSightRange() {
        return sightRange;
    }

    public boolean isFlying() {
        return flying;
    }

    public long getLastAttackTS() {
        return lastAttackTS;
    }

    public double getDmgPotential(GameStructures gs) {
        //dmgpotential = dps / time to closest tower
        double dps = this.damage / this.attackSpeed;
        Building[] towers = gs.getEnemyTowers(this.getTeamNo());
        double dist = 99;
        for(int i = 0; i < towers.length; i++){
            if (dist > distance(towers[i])) {
                dist = distance(towers[i]);
            }
        }
        double time = dist/this.moveSpeed;
        this.dmgPotential = dps/time;
        return dmgPotential;
    }

    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }

    public void setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setLastAttackTS(long lastAttackTS) {
        this.lastAttackTS = lastAttackTS;
    }

    public void draw(Color TeamColor, int teamDrawReq){
//        if(teamDrawReq == this.getTeamNo()) {
            StdDraw.setPenColor((isFlying()) ? SHADOW_GREY : TeamColor);
            StdDraw.filledEllipse(this.getPos()[0], this.getPos()[1], this.getWidth() / 2, this.getHeight() / 2);
            if (this.isFlying()) {
                StdDraw.setPenColor(TeamColor);
                StdDraw.filledEllipse(this.getPos()[0], this.getPos()[1] + (1.85 * (this.getHeight() / 2)), this.getWidth() / 2, this.getHeight() / 2);
            }
//        } else {
//            StdDraw.setPenColor((isFlying()) ? SHADOW_GREY : TeamColor);
//            StdDraw.filledEllipse(GameBoard.getXdim()-this.getPos()[0], GameBoard.getYdim()-this.getPos()[1], this.getWidth() / 2, this.getHeight() / 2);
//            if (this.isFlying()) {
//                StdDraw.setPenColor(TeamColor);
//                StdDraw.filledEllipse(GameBoard.getXdim()-this.getPos()[0], GameBoard.getYdim()-this.getPos()[1] + (1.85 * (this.getHeight() / 2)), this.getWidth() / 2, this.getHeight() / 2);
//            }
//        }
        drawHealthbar(teamDrawReq);
    }

    private void drawHealthbar(int teamDrawReq){
        StdDraw.setPenColor(StdDraw.GREEN);
//        if(teamDrawReq == this.getTeamNo()) {
            StdDraw.filledRectangle(this.getPos()[0], this.getPos()[1] + (1.25 * (this.getHeight() / 2)), this.getWidth() / 2, 0.1);
            StdDraw.setPenColor(StdDraw.RED);
            double healthLost = (double) (this.getHP() - this.getCurrentHP()) / (double) this.getHP();
            double halfWidth = healthLost * this.getWidth() / 2;
            double x = this.getPos()[0] + this.getWidth() / 2 - halfWidth;
            StdDraw.filledRectangle(x, this.getPos()[1] + (1.25 * (this.getHeight() / 2)), halfWidth, 0.1);
//        } else {
//            StdDraw.filledRectangle(GameBoard.getXdim()-this.getPos()[0], GameBoard.getYdim()-(this.getPos()[1] + (1.25 * (this.getHeight() / 2))), this.getWidth() / 2, 0.1);
//            StdDraw.setPenColor(StdDraw.RED);
//            double healthLost = (double) (this.getHP() - this.getCurrentHP()) / (double) this.getHP();
//            double halfWidth = healthLost * this.getWidth() / 2;
//            double x = GameBoard.getXdim() - (this.getPos()[0] + this.getWidth() / 2 - halfWidth);
//            StdDraw.filledRectangle(x, GameBoard.getYdim()-(this.getPos()[1] + (1.25 * (this.getHeight() / 2))), halfWidth, 0.1);
//        }
    }

    public void updatePos(GameStructures towers){
        long currentTime = System.currentTimeMillis();
        double timePast = (double)(currentTime - this.getUpdateTS())/GameBoard.getSecs();
        if(!isFlying()){
            if(GamePath.onPath(this.getPos()[0], this.getPos()[1])){
                //Case for when on path
                String inst = GamePath.getInstruction(this.getPos()[0], this.getPos()[1]);
                switch(inst){
                    case "right":
                        this.setPos((getTeamNo()==1)?this.getPos()[0]+(timePast*this.getMoveSpeed()):this.getPos()[0]-(timePast*this.getMoveSpeed()), this.getPos()[1]);
                        break;
                    case "left":
                        this.setPos((getTeamNo()==1)?this.getPos()[0]-(timePast*this.getMoveSpeed()):this.getPos()[0]+(timePast*this.getMoveSpeed()), this.getPos()[1]);
                        break;
                    case "up":
                        this.setPos(this.getPos()[0], (getTeamNo()==1)?this.getPos()[1]+(timePast*this.getMoveSpeed()):this.getPos()[1]-(timePast*this.getMoveSpeed()));
                        break;
                    default:
                        System.out.println("Path Error: This line should not be printing\nError thrown at pos: [" + this.getPos()[0] + ", " + this.getPos()[1] + "]");
                }
            } else {
               //Case for when not on path
                if(getTeamNo()==1) {
                    if (this.getPos()[0] <= 3.5 || (this.getPos()[0] > 9.0 && this.getPos()[0] <= 14.5)) {
                        this.setPos(this.getPos()[0] + (timePast * this.getMoveSpeed() * Math.cos(Math.PI / 6)), this.getPos()[1] + (timePast * this.getMoveSpeed() * Math.sin(Math.PI / 6)));
                    } else {
                        this.setPos(this.getPos()[0] - (timePast * this.getMoveSpeed() * Math.cos(Math.PI / 6)), this.getPos()[1] + (timePast * this.getMoveSpeed() * Math.sin(Math.PI / 6)));
                    }
                } else {
                    if (this.getPos()[0] <= 3.5 || (this.getPos()[0] > 9.0 && this.getPos()[0] <= 14.5)) {
                        this.setPos(this.getPos()[0] + (timePast * this.getMoveSpeed() * Math.cos(Math.PI / 6)), this.getPos()[1] - (timePast * this.getMoveSpeed() * Math.sin(Math.PI / 6)));
                    } else {
                        this.setPos(this.getPos()[0] - (timePast * this.getMoveSpeed() * Math.cos(Math.PI / 6)), this.getPos()[1] - (timePast * this.getMoveSpeed() * Math.sin(Math.PI / 6)));
                    }
                }
            }
        } else {
            //Standard move case for flying units

            //======================================================
            //Initial flawed logic
//            double xPosDiff, yPosDiff;
//            switch(towers.getEnemyTowerState(this.getTeamNo())){
//                case "111":
//                    if((this.getPos()[0] < 9.0 && getTeamNo() == 1) || (this.getPos()[0] > 9.0 && getTeamNo() == 2)){
//                        xPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[2][0] - this.getPos()[0];
//                        yPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[2][1] - this.getPos()[1];
//                    } else {
//                        xPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[0][0] - this.getPos()[0];
//                        yPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[0][1] - this.getPos()[1];
//                    }
//                    break;
//                case "011":
//                    if((this.getPos()[0] < 9.0 && getTeamNo() == 1) || (this.getPos()[0] > 9.0 && getTeamNo() == 2)){
//                        xPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[2][0] - this.getPos()[0];
//                        yPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[2][1] - this.getPos()[1];
//                    } else {
//                        xPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[1][0] - this.getPos()[0];
//                        yPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[1][1] - this.getPos()[1];
//                    }
//                    break;
//                case "110":
//                    if((this.getPos()[0] < 9.0 && getTeamNo() == 1) || (this.getPos()[0] > 9.0 && getTeamNo() == 2)){
//                        xPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[1][0] - this.getPos()[0];
//                        yPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[1][1] - this.getPos()[1];
//                    } else {
//                        xPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[2][0] - this.getPos()[0];
//                        yPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[2][1] - this.getPos()[1];
//                    }
//                    break;
//                default:    //Encompasses case "010" too
//                    xPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[1][0] - this.getPos()[0];
//                    yPosDiff = towers.getEnemyTowerPos(this.getTeamNo())[1][1] - this.getPos()[1];
//            }
            //===============================================================

            //===============================================================
            //New logic

            double xPosDiff = 0, yPosDiff = 0;
            double[] xDiffs = new double[3];
            double[] yDiffs = new double[3];
            double minEucDist = 9999999999.9;
            for(int i = 0; i < 3; i++){
                //Calculate X & Y differences between unit and tower
                xDiffs[i] = towers.getEnemyTowerPos(this.getTeamNo())[i][0] - this.getPos()[0];
                yDiffs[i] = towers.getEnemyTowerPos(this.getTeamNo())[i][1] - this.getPos()[1];

                //Calculate euclidean distance to each tower
                double euclideanDist = Math.pow(Math.pow(xDiffs[i], 2) + Math.pow(yDiffs[i], 2), 0.5);

                //Check if closest, and if tower is still alive
                if(euclideanDist < minEucDist && towers.getEnemyTowerState(this.getTeamNo()).substring(i, i+1) != "0"){
                    //update min dist
                    minEucDist = euclideanDist;
                    //update with current x & y diffs
                    xPosDiff = xDiffs[i];
                    yPosDiff = yDiffs[i];
                }
            }
            double atan = Math.atan(yPosDiff/xPosDiff);
            //Cases for direction needed to be moved in, workaround for signage of trig functions
//            if(getTeamNo()==1) {
                if (xPosDiff < 0 && yPosDiff < 0) {
                    this.setPos(this.getPos()[0] - (timePast * this.getMoveSpeed() * Math.cos(atan)), this.getPos()[1] + (timePast * this.getMoveSpeed() * Math.sin(atan)));
                } else if (xPosDiff < 0) {
                    this.setPos(this.getPos()[0] - (timePast * this.getMoveSpeed() * Math.cos(atan)), this.getPos()[1] - (timePast * this.getMoveSpeed() * Math.sin(atan)));
                } else if (yPosDiff < 0) {
                    this.setPos(this.getPos()[0] + (timePast * this.getMoveSpeed() * Math.cos(atan)), this.getPos()[1] + (timePast * this.getMoveSpeed() * Math.sin(atan)));
                } else {
                    this.setPos(this.getPos()[0] + (timePast * this.getMoveSpeed() * Math.cos(atan)), this.getPos()[1] + (timePast * this.getMoveSpeed() * Math.sin(atan)));
                }
//            } else {
//                if (xPosDiff < 0 && yPosDiff < 0) {
//                    this.setPos(this.getPos()[0] + (timePast * this.getMoveSpeed() * Math.cos(atan)), this.getPos()[1] - (timePast * this.getMoveSpeed() * Math.sin(atan)));
//                } else if (xPosDiff < 0) {
//                    this.setPos(this.getPos()[0] + (timePast * this.getMoveSpeed() * Math.cos(atan)), this.getPos()[1] + (timePast * this.getMoveSpeed() * Math.sin(atan)));
//                } else if (yPosDiff < 0) {
//                    this.setPos(this.getPos()[0] - (timePast * this.getMoveSpeed() * Math.cos(atan)), this.getPos()[1] - (timePast * this.getMoveSpeed() * Math.sin(atan)));
//                } else {
//                    this.setPos(this.getPos()[0] - (timePast * this.getMoveSpeed() * Math.cos(atan)), this.getPos()[1] - (timePast * this.getMoveSpeed() * Math.sin(atan)));
//                }
//            }
        }
        this.setUpdateTS(currentTime);
    }

    // Method for checking the enemy team and if any can be attacked or seen
    private boolean aggroMove(GameStructures towers, TeamLL enemy){
        //Update states of potential targets
        enemy.reapDead();
        towers.updateStates((enemy.getTeamNo()==2)?1:2);
//        //Check if already aggroed on a unit
//        if(targetUnit == null && targetBuilding != null){
//            if(distance(targetBuilding) <= attackRange || distance(targetBuilding) <= attackRange){
//                attack();
//            } else if(distance(targetBuilding) <= sightRange || distance(targetBuilding) <= sightRange){
//                chase();
//            }
//            return true;
//        } else if(targetUnit != null && targetBuilding == null){
//            if(distance(targetUnit) <= attackRange || distance(targetUnit) <= attackRange){
//                attack();
//            } else if(distance(targetUnit) <= sightRange || distance(targetUnit) <= sightRange){
//                chase();
//            }
//            return true;
//        }
    
        // Create arrays of troops and buildings to scan through
        Troop[] inAttackRangeTr = new Troop[60];
        Troop[] inSightRangeTr = new Troop[60];
        Building[] inAttackRangeBd = new Building[60];
        Building[] inSightRangeBd = new Building[60];
        int inAtT = 0, inSghtT = 0, inAtB = 0, inSghtB = 0;
        double dist;

        //Check distance to towers
        for(Building tw : towers.getEnemyTowers(this.getTeamNo())){
            dist = distance(tw);
            if(dist <= getAttackRange()){
                inAttackRangeBd[inAtB] = tw;
                inSightRangeBd[inSghtB] = tw;
                inAtB++;
                inSghtB++;
            } else if(dist <= getSightRange()){
                inSightRangeBd[inSghtB] = tw;
                inSghtB++;
            }
        }

        //Perform same for played buildings
        for(Building cd : enemy.getListBuildings()){
            dist = distance(cd);
            if(dist <= getAttackRange()){
                inAttackRangeBd[inAtB] = cd;
                inSightRangeBd[inSghtB] = cd;
                inAtB++;
                inSghtB++;
            } else if(dist <= getSightRange()){
                inSightRangeBd[inSghtB] = cd;
                inSghtB++;
            }
        }

        //Perform same for played units
        for(Troop tr : enemy.getListUnits()){
            dist = distance(tr);
            if(dist <= getAttackRange()){
                inAttackRangeTr[inAtT] = tr;
                inSightRangeTr[inSghtT] = tr;
                inAtT++;
                inSghtT++;
            } else if(dist <= getSightRange()){
                inSightRangeTr[inSghtT] = tr;
                inSghtT++;
            }
        }

        // If none in range then return false
        if(inSghtB == 0 && inSghtT == 0){
            return false;
        }

        //If some are attack-able, attack the closest
        //First check the buildings
        if(inAtB > 0){
            //Find closest
            Building closest = null;
            double minDist = 9999;
            for(int i = 0; inAttackRangeBd[i] != null; i++){
                if(i == 0){
                    closest = inAttackRangeBd[i];
                    minDist = distance(inAttackRangeBd[i]);
                } else {
                    if(minDist > distance(inAttackRangeBd[i])){
                        closest = inAttackRangeBd[i];
                        minDist = distance(inAttackRangeBd[i]);
                    }
                }
            }
            targetBuilding = closest;
        } else {
            //If not attack-able check those visible to move to
            Building closest = null;
            double minDist = 9999;
            for(int i = 0; inSightRangeBd[i] != null; i++){
                if(i == 0){
                    closest = inSightRangeBd[i];
                    minDist = distance(inSightRangeBd[i]);
                } else {
                    if(minDist > distance(inSightRangeBd[i])){
                        closest = inSightRangeBd[i];
                        minDist = distance(inSightRangeBd[i]);
                    }
                }
            }
            targetBuilding = closest;
        }

        //Then check the troops
        if(inAtT > 0){
            //Find closest
            Troop closest = null;
            double minDist = 9999;
            for(int i = 0; inAttackRangeTr[i] != null; i++){
                if(i == 0){
                    closest = inAttackRangeTr[i];
                    minDist = distance(inAttackRangeTr[i]);
                } else {
                    if(minDist > distance(inAttackRangeTr[i])){
                        closest = inAttackRangeTr[i];
                        minDist = distance(inAttackRangeTr[i]);
                    }
                }
            }
            targetUnit = closest;
        } else {
            //If not attack-able check those visible to move to
            Troop closest = null;
            double minDist = 9999;
            for(int i = 0; inSightRangeTr[i] != null; i++){
                if(i == 0){
                    closest = inSightRangeTr[i];
                    minDist = distance(inSightRangeTr[i]);
                } else {
                    if(minDist > distance(inSightRangeTr[i])){
                        closest = inSightRangeTr[i];
                        minDist = distance(inSightRangeTr[i]);
                    }
                }
            }
            targetUnit = closest;
        }

        //Check both targets to see which is closer
        if(targetUnit != null && targetBuilding != null){
            if(this.distance(targetBuilding) <= this.distance(targetUnit)){
                targetUnit = null;
                if(this.distance(targetBuilding) <= getAttackRange()){
                    attack();
                } else {
                    chase();
                }
            } else {
                targetBuilding = null;
                if(this.distance(targetUnit) <= getAttackRange()){
                    attack();
                } else {
                    chase();
                }
            }
        } else if(targetBuilding == null){
            if(this.distance(targetUnit) <= getAttackRange()){
                attack();
            } else {
                chase();
            }
        } else if(targetUnit == null){
            if(this.distance(targetBuilding) <= getAttackRange()){
                attack();
            } else {
                chase();
            }
        }
        return true;
    }

    //Private helper to handle attacking a unit
    private void attack(){
        long currentTime = System.currentTimeMillis();
        if((double)(currentTime - getLastAttackTS())/GameBoard.getSecs() >= attackSpeed){
            this.setLastAttackTS(System.currentTimeMillis());
            if(targetUnit == null){
                targetBuilding.setCurrentHP(targetBuilding.getCurrentHP() - this.getDamage());
                if(targetBuilding.getCurrentHP() <= 0){
                    targetBuilding = null;
                }
            } else {
                targetUnit.setCurrentHP(targetUnit.getCurrentHP() - this.getDamage());
                if(targetUnit.getCurrentHP() <= 0){
                    targetUnit = null;
                }
            }
            this.setLastAttackTS(currentTime);
            this.setUpdateTS(currentTime);
        }
    }

    //Private helper to handle moving towards target
    private void chase(){
        long currentTime = System.currentTimeMillis();
        double timePast = (double)(currentTime - this.getUpdateTS())/GameBoard.getSecs();

        if(targetUnit == null){
            //Check if dead
            if(targetBuilding.getCurrentHP() <= 0){
                targetBuilding = null;
            } else {
                //if not move towards target
                double xPosDiff = targetBuilding.getPos()[0] - this.getPos()[0];
                double yPosDiff = targetBuilding.getPos()[1] - this.getPos()[1];
                double atan = Math.atan(yPosDiff/xPosDiff);
                //Cases for direction needed to be moved in, workaround for signage of trig functions
                if(xPosDiff < 0 && yPosDiff < 0){
                    this.setPos((this.getPos()[0]-(timePast*this.getMoveSpeed()*Math.cos(atan))),(this.getPos()[1]+(timePast*this.getMoveSpeed()*Math.sin(atan))));
                } else if(xPosDiff < 0){
                    this.setPos((this.getPos()[0]-(timePast*this.getMoveSpeed()*Math.cos(atan))),(this.getPos()[1]-(timePast*this.getMoveSpeed()*Math.sin(atan))));
                } else if(yPosDiff < 0){
                    this.setPos((this.getPos()[0]+(timePast*this.getMoveSpeed()*Math.cos(atan))),(this.getPos()[1]+(timePast*this.getMoveSpeed()*Math.sin(atan))));
                } else {
                    this.setPos((this.getPos()[0]+(timePast*this.getMoveSpeed()*Math.cos(atan))),(this.getPos()[1]+(timePast*this.getMoveSpeed()*Math.sin(atan))));
                }
            }
        } else {
            //Check if dead
            if(targetUnit.getCurrentHP() <= 0){
                targetUnit = null;
            } else {
                //if not move towards target
                double xPosDiff = targetUnit.getPos()[0] - this.getPos()[0];
                double yPosDiff = targetUnit.getPos()[1] - this.getPos()[1];
                double atan = Math.atan(yPosDiff/xPosDiff);
                //Cases for direction needed to be moved in, workaround for signage of trig functions
                if(xPosDiff < 0 && yPosDiff < 0){
                    this.setPos(this.getPos()[0]-(timePast*this.getMoveSpeed()*Math.cos(atan)),this.getPos()[1]+(timePast*this.getMoveSpeed()*Math.sin(atan)));
                } else if(xPosDiff < 0){
                    this.setPos(this.getPos()[0]-(timePast*this.getMoveSpeed()*Math.cos(atan)),this.getPos()[1]-(timePast*this.getMoveSpeed()*Math.sin(atan)));
                } else if(yPosDiff < 0){
                    this.setPos(this.getPos()[0]+(timePast*this.getMoveSpeed()*Math.cos(atan)),this.getPos()[1]+(timePast*this.getMoveSpeed()*Math.sin(atan)));
                } else {
                    this.setPos(this.getPos()[0]+(timePast*this.getMoveSpeed()*Math.cos(atan)),this.getPos()[1]+(timePast*this.getMoveSpeed()*Math.sin(atan)));
                }
            }
        }
        setUpdateTS(currentTime);
    }

    // Update troop, check for aggressive move, if none available move normally
    public void update(GameStructures towers, TeamLL enemy){
        boolean aggroed = aggroMove(towers, enemy);
        if(!aggroed){
            this.updatePos(towers);
        }
    }
}
