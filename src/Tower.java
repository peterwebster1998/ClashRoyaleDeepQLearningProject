/*
Author: Peter Webster
Date: 5/12/20
 */

public class Tower extends Building{

    private int damage;
    private double range, attackSpeed;
    private long lastAttackTS;
    private Building targetBuilding = null;
    private Troop targetUnit = null;

    public Tower(int teamNo, int cost, int ID, double xPos, double yPos, long currentTime, double width, double height,
                 int HP, int lifeSpan,
                 int damage, double range, double attackSpeed){
        super(teamNo, cost, ID, xPos, yPos, currentTime, width, height, HP, lifeSpan);
        this.damage = damage;
        this.range = range;
        this.attackSpeed = attackSpeed;
        this.lastAttackTS = currentTime;
    }

    public int getDamage() {
        return damage;
    }

    public double getRange(){
        return range;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public long getLastAttackTS(){
        return lastAttackTS;
    }

    public void setLastAttackTS(long timeStamp){
        this.lastAttackTS = timeStamp;
    }

    public void update(GameStructures towers, TeamLL enemy){
        //Update HPs based on time in the arena
        updateLife();
        //Scans other team to see if any cards in range
        lookout(towers, enemy);
    }

    private boolean lookout(GameStructures towers, TeamLL enemy){
        //Update states of potential targets
        enemy.reapDead();
        towers.updateStates((enemy.getTeamNo()==2)?1:2);
        //Check if already targeted on a unit
        if(targetUnit != null ^ targetBuilding != null){
            if(targetBuilding == null) {
                //If unit is in range, attack it again
                if (distance(targetUnit) <= range){
                    attack();
                }
            } else if(targetUnit == null){
                if(distance(targetBuilding) <= range){
                    attack();
                }
            }
            return true;
        }

        // Create arrays of troops and buildings to scan through
        Troop[] inAttackRangeTr = new Troop[60];
        Building[] inAttackRangeBd = new Building[60];
        int inAtT = 0, inAtB = 0;
        double dist;

        //Check distance to towers
        for(Building tw : towers.getEnemyTowers(this.getTeamNo())){
            dist = distance(tw);
            if(dist <= getRange()){
                inAttackRangeBd[inAtB] = tw;
                inAtB++;
            }
        }

        //Perform same for played buildings
        for(Building cd : enemy.getListBuildings()){
            dist = distance(cd);
            if(dist <= getRange()){
                inAttackRangeBd[inAtB] = cd;
                inAtB++;
            }
        }

        //Perform same for played units
        for(Troop tr : enemy.getListUnits()){
            dist = distance(tr);
            if(dist <= getRange()){
                inAttackRangeTr[inAtT] = tr;
                inAtT++;
            }
        }

        // If none in range then return false
        if(inAtB == 0 && inAtT == 0){
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
        }

        //Check both targets to see which is closer
        if(targetUnit != null && targetBuilding != null){
            if(this.distance(targetBuilding) <= this.distance(targetUnit)){
                targetUnit = null;
            } else {
                targetBuilding = null;
            }
        }

        if(targetUnit != null ^ targetBuilding != null){
            attack();
        }
        return true;
    }

    private void attack(){
        long currentTime = System.currentTimeMillis();
        if((currentTime - getLastAttackTS())/GameBoard.getSecs() >= attackSpeed){
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
        }
    }
}
