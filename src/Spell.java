/*
Author: Peter Webster
Date: 5/12/20
 */

public class Spell extends Card{

    private int damage, numAttacks;
    private double castRadius, castSpeed;

    public Spell(int teamNo, int cost, int ID, double xPos, double yPos, long currentTime, double width, double height,
                 int damage, double castRadius, double castSpeed, int numAttacks){
        super(teamNo, cost, ID, xPos, yPos, currentTime, width, height);
        this.damage = damage;
        this.castRadius = castRadius;
        this.castSpeed = castSpeed;
        this.numAttacks = numAttacks;
    }

    public int getDamage() {
        return damage;
    }

    public double getCastRadius() {
        return castRadius;
    }

    public double getCastSpeed() {
        return castSpeed;
    }

    public int getNumAttacks() {
        return numAttacks;
    }

    public void attack(TeamLL enemy, GameStructures gs){
        Troop[] units = enemy.getListUnits();
        Building[] buildings = enemy.getListBuildings();
        Building[] towers = gs.getEnemyTowers(getTeamNo());

        for(Troop t : units){
            if(distance(t) < castRadius){
                t.setCurrentHP(t.getCurrentHP()-(this.getDamage()*this.getNumAttacks()));
            }
        }

        for(Building b : buildings){
            if(distance(b) < castRadius){
                b.setCurrentHP(b.getCurrentHP()-(this.getDamage()*this.getNumAttacks()));
            }
        }

        for(Building b : towers){
            if(distance(b) < castRadius){
                b.setCurrentHP(b.getCurrentHP()-(this.getDamage()*this.getNumAttacks()));
            }
        }
    }
}
