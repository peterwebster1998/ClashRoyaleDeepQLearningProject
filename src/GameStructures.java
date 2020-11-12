import java.awt.*;
import java.io.Serializable;

/*
Author: Peter Webster
Date: 5/24/20

5/25/2020 UPDATE: Added draw method, added getEnemyTowers() method
 */
public class GameStructures implements Serializable {

    private Tower[][] towers;
    private boolean[][] towerStates;
    private long startTS = 0;
    private static final double XDIM = 18.0;
    private static final double YDIM = 32.0;
    private static final Color TEAM1_BLUE = new Color(0, 120, 250);
    private static final Color TEAM2_PURP = new Color(75, 0, 255);

    //Creates all Towers at the beginning of the game
    public GameStructures(){
        this.towers = new Tower[2][3];
        this.towerStates = new boolean[2][3];
        this.startTS = System.currentTimeMillis();

        //Create King and Princess Towers for both teams at the beginning of the game
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){
                //Calculate starting position   --  FYI: Written as practice with single line conditionals
                double xPos = (j==1) ? 9.0 : (j>1) ? XDIM-3.5 : 3.5;
                double yPos = (j==1) ? 3.0 : 6.5;
                xPos = (i==1) ? XDIM-xPos : xPos;
                yPos = (i==1) ? YDIM-yPos : yPos;

                //Create individual towers
                this.towers[i][j] = new Tower(i+1, 0, (j==1) ? 999 : 998, xPos, yPos, startTS, (j==1) ? 4 : 3, (j==1) ? 4 : 3,
                        (j==1) ? 4008 : 2534, 99999999, 90, (j==1) ? 7 :7.5, (j==1) ? 1.0:0.8);
                this.towers[i][j].setPos(xPos, yPos);
                this.towerStates[i][j] = true;
            }
        }
    }

    public double[] legalPlacement(Double[] pos, int reqTeamNo){

        if(this.towerStates[(reqTeamNo==1)?1:0][0] && this.towerStates[(reqTeamNo==1)?1:0][2]){
            if(pos[1] >= 15.0){
                System.out.println("Making Adjustment: Player " + reqTeamNo + " [" + pos[0] + ", " + pos[1] + "] --> [" + pos[0] + ", " + 14.5 + "]");
                pos[1] = 14.5;
            }
        } else if(this.towerStates[(reqTeamNo==1)?1:0][0]){
            if(pos[1] >= 15.0 && pos[0] <= GameBoard.getXdim()/2){
                System.out.println("Making Adjustment: Player " + reqTeamNo + " [" + pos[0] + ", " + pos[1] + "] --> [" + pos[0] + ", " + 14.5 + "]");
                pos[1] = 14.5;
            } else if(pos[1] >= 21.0 && pos[0] > GameBoard.getXdim()/2){
                System.out.println("Making Adjustment: Player " + reqTeamNo + " [" + pos[0] + ", " + pos[1] + "] --> [" + pos[0] + ", " + 20.5 + "]");
                pos[1] = 20.5;
            }
        } else if(this.towerStates[(reqTeamNo==1)?1:0][2]) {
            if (pos[1] >= 15.0 && pos[0] >= GameBoard.getXdim() / 2) {
                System.out.println("Making Adjustment: Player " + reqTeamNo + " [" + pos[0] + ", " + pos[1] + "] --> [" + pos[0] + ", " + 14.5 + "]");
                pos[1] = 14.5;
            } else if (pos[1] >= 21.0 && pos[0] < GameBoard.getXdim() / 2) {
                System.out.println("Making Adjustment: Player " + reqTeamNo + " [" + pos[0] + ", " + pos[1] + "] --> [" + pos[0] + ", " + 20.5 + "]");
                pos[1] = 20.5;
            }
        }
        double[] xy = new double[2];
        xy[0] = (int)(double)pos[0] + 0.5;
        xy[1] = (int)(double)pos[1] + 0.5;
        return xy;
    }

    public String gameWon(){
        if(!towerStates[0][1]){
            return "Player 2 Wins!";
        } else if(!towerStates[1][1]){
            return "Player 1 Wins!";
        } else {
            return "Game Still Going...";
        }
    }

    public void gameWinBanner(String outcome){
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.filledRectangle(XDIM/2, YDIM/2, XDIM/2, YDIM/6);
        StdDraw.setPenColor(StdDraw.YELLOW);
        StdDraw.text(XDIM/2, YDIM/2, outcome);
        StdDraw.show();
    }

    public void updateStates(int teamNo){
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){
                if(towers[(teamNo==1)?i:1-i][j] != null) {
                    if (towers[(teamNo==1)?i:1-i][j].getCurrentHP() <= 0) {
                        towerStates[(teamNo==1)?i:1-i][j] = false;
                        towers[(teamNo==1)?i:1-i][j] = null;
                    }
                }
            }
        }
    }

    public void updateTowers(TeamLL plyr1, TeamLL plyr2){
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){
                if(towers[i][j] != null) {
                    towers[i][j].update(this, (i==0)?plyr2:plyr1);
                }
            }
        }
    }

    public String getEnemyTowerState(int teamNo){
        String towerState = "";
        updateStates(teamNo);
//        if(teamNo == 1){
            for(int i = 0; i < 3; i++){
                towerState = towerState.concat((towerStates[1][i]) ? "1" : "0");
            }
//        } else {
//            //Reverses order if giving information to team 2
//            for(int i = 2; i > -1; i--){
//                towerState = towerState.concat((towerStates[0][i]) ? "1" : "0");
//            }
//        }
        return towerState;
    }

    public double[][] getEnemyTowerPos(int teamNo){
        double[][] towerCoords = new double[3][2];
        for(int i = 0; i < 3; i++){
            if(towerStates[(teamNo == 2) ? 0 : 1][(teamNo == 2) ? 2-i : i]){
                towerCoords[i] = towers[(teamNo == 2) ? 0 : 1][(teamNo == 2) ? 2-i : i].getPos();
                // Reverse coordinate system for team 2
//                if (teamNo == 2) {
//                    towerCoords[i][0] = XDIM - towerCoords[i][0];
//                    towerCoords[i][1] = YDIM - towerCoords[i][1];
//                }
            }
        }
        return towerCoords;
    }

    // for use in the Troop.aggroMove() method
    public Building[] getEnemyTowers(int teamNo){
        int towerCnt = 0;
        Building[] towerLst;

        //Cnt living towers
        for(int i = 0; i < 3 ; i++){
            if(towerStates[(teamNo==1)?1:0][i]){
                towerCnt++;
            }
        }
        //Instantiate array size to towerCnt
        towerLst = new Building[towerCnt];
        int idx = 0;
        for(int i =0; i < 3; i++){
            if(towerStates[(teamNo==1)?1:0][i]){
                towerLst[idx] = towers[(teamNo==1)?1:0][i];
                idx++;
            }
        }
        return towerLst;
    }

    public void draw(int teamNo){
        //Draw own towers
        for(int i = 0; i < 3; i++){
            if(towers[(teamNo == 1) ? 0 : 1][i] != null) {
                towers[(teamNo == 1) ? 0 : 1][i].draw(TEAM1_BLUE, teamNo);
            }
        }

        //Draw opposition towers
        for(int i = 0; i < 3; i++){
            if(towers[(teamNo == 1) ? 1 : 0][i] != null) {
                towers[(teamNo == 1) ? 1 : 0][i].draw(TEAM2_PURP, teamNo);
            }
        }

        //Draw Characteristic crown on the crown towers
        StdDraw.setPenColor(StdDraw.YELLOW);
        for(Tower[] ts : towers){
            for(Tower t : ts){
                if(t != null) {
                    double[] Xcrown = {t.getPos()[0] - t.getWidth() / 4, t.getPos()[0] - t.getWidth() / 4, t.getPos()[0] - t.getWidth() / 8, t.getPos()[0],
                            t.getPos()[0] + t.getWidth() / 8, t.getPos()[0] + t.getWidth() / 4, t.getPos()[0] + t.getWidth() / 4, t.getPos()[0] - t.getWidth() / 4};
                    double[] Ycrown = {t.getPos()[1] - t.getHeight() / 4, t.getPos()[1] + t.getHeight() / 4, t.getPos()[1] + t.getHeight() / 8, t.getPos()[1] + t.getHeight() / 4,
                            t.getPos()[1] + t.getHeight() / 8, t.getPos()[1] + t.getHeight() / 4, t.getPos()[1] - t.getHeight() / 4, t.getPos()[1] - t.getHeight() / 4};
                    StdDraw.filledPolygon(Xcrown, Ycrown);
                }
            }
        }

//        StdDraw.setPenColor(StdDraw.BLACK);
//        for(int i = 0; i < 2; i++){
//            for(int j = 0; j < 3; j++){
//                if(towerStates[i][j]) {
//                    StdDraw.text(towers[i][j].getPos()[0], towers[i][j].getPos()[1], String.valueOf(j));
//                }
//            }
//        }
    }

    //Get stats for network
    public double[][] AIstats(int player){
        double[][] towerStats = new double[2][3];
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){
                if(towerStates[i][j]) {
                    towerStats[(player == 1) ? i : 1 - i][j] = towers[i][j].getCurrentHP();
                } else {
                    towerStats[(player==1)?i:1-i][j] = 0;
                }
            }
        }
        return towerStats;
    }
}
