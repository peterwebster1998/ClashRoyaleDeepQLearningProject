/*
Author: Peter Webster
Date: 5/12/20
 */

public class Spawner extends Building{

    private int spawnUnit;
    private double spawnTime;
    private long lastSpawnTS;   //Time Stamp

    public Spawner(int teamNo, int cost, int ID, double xPos, double yPos, long currentTime, double width, double height,
                   int HP, int lifeSpan,
                   int spawnUnitID, double spawnTime){
        super(teamNo, cost, ID, xPos, yPos, currentTime, width, height, HP, lifeSpan);
        this.spawnUnit = spawnUnitID;
        this.spawnTime = spawnTime;
        this.lastSpawnTS = 0;   // May have to change to currentTime
    }

    public int getSpawnUnitID() {
        return spawnUnit;
    }

    public double getSpawnTime() {
        return spawnTime;
    }

    public long getLastSpawnTS() {
        return lastSpawnTS;
    }

    public void setLastSpawnTS(long lastSpawnTS) {
        this.lastSpawnTS = lastSpawnTS;
    }
}
