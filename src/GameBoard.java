/*
Author: Peter Webster
Date: 5/14/20

5/18/2020 UPDATE: Implemented deck.Draw() method in Draw()
5/31/2020 UPDATE: Added static getXDIM & getYDIM methods
 */

import java.awt.*;
import java.io.Serializable;

public class GameBoard implements Serializable {

    private static final long SECS = 1000;
    private static final double XDIM = 18.0;
    private static final double YDIM = 32.0;
    private static final Color BRIDGE_BROWN = new Color(155, 90, 20);
    private static final Color RIVER_BLUE = new Color(50, 150, 255);
    private static final Color HAND_BACKGROUND_BLUE = new Color(0, 100, 255);
    private static GameClock clk = new GameClock();
    private static int gameTimePassed;
    private static GamePath path = new GamePath();
    private Deck p1, p2;

    public GameBoard(Deck p1, Deck p2) {
        // Store players deck to visualize hand
        this.p1 = p1;
        this.p2 = p2;
        this.gameTimePassed = 0;

        //Enable Double Buffering
        StdDraw.enableDoubleBuffering();
        //Set canvas size and Scale
        StdDraw.setCanvasSize(550, 1000);
        StdDraw.setXscale(0.0, XDIM);
        StdDraw.setYscale(-(YDIM)/9, YDIM);

        //Start Elixir flow
        p1.gameStart();
        p2.gameStart();
    }

    public void draw(int teamNo){
        //Clear Canvas
        StdDraw.clear();
        //Draw the checkerboard pattern on the canvas
        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        for (int i = 0; i < YDIM; ++i) {
            for (int j = 0; j < XDIM; ++j) {
                if ((i % 2 == 0 && j % 2 == 0) || (i % 2 == 1 && j % 2 == 1)) {
                    StdDraw.filledSquare(j + 0.5, i + 0.5, 0.5);
                }
            }
        }

        //For the realistic green board
//        for (int i = 0; i < YDIM; ++i) {
//            for (int j = 0; j < XDIM; ++j) {
//                if ((i % 2 == 0 && j % 2 == 0) || (i % 2 == 1 && j % 2 == 1)) {
//                    StdDraw.setPenColor(StdDraw.GREEN);
//                } else {
//                    StdDraw.setPenColor(150, 255, 50);
//                }
//                StdDraw.filledSquare(j + 0.5, i + 0.5, 0.5);
//            }
//        }

        //Draw the River
        StdDraw.setPenColor(RIVER_BLUE);
        StdDraw.filledRectangle(XDIM/2, YDIM/2, XDIM/2, 1.0);

        //Draw the Bridges
        StdDraw.setPenColor(BRIDGE_BROWN);
        StdDraw.filledRectangle(3.5, YDIM/2, 1.3, 1.15);
        StdDraw.filledRectangle(XDIM - 3.5, YDIM/2, 1.3, 1.15);

        //Add Black Space in corners where units are unplaceable
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(3.0, 0.5, 3.0, 0.5);
        StdDraw.filledRectangle(XDIM - 3.0, 0.5, 3.0, 0.5);
        StdDraw.filledRectangle(3.0, YDIM - 0.5, 3.0, 0.5);
        StdDraw.filledRectangle(XDIM - 3.0, YDIM - 0.5, 3.0, 0.5);

        //Draw deck background
        StdDraw.setPenColor(HAND_BACKGROUND_BLUE);
        StdDraw.filledRectangle(XDIM/2, -0.5*(YDIM/9),XDIM/2, 0.5*(YDIM/9));

        //Draw Game Clock
        clk.draw();

        //Draw path
        path.draw();

        //Draw hand
        if(teamNo == 1){
            p1.draw();
            p2.drawElixir();
        } else {
            p2.draw();
            p1.drawElixir();
        }


        //StdDraw.show();
    }

    public static double getXdim(){
        return XDIM;
    }

    public static double getYdim(){
        return YDIM;
    }

    public static int getGameTimePassed() { return gameTimePassed; }

    private static class GameClock{

        private long gameStartTS, currentTS;

        private GameClock(){
            this.gameStartTS = System.currentTimeMillis();
            this.currentTS = System.currentTimeMillis();
        }

        private String gameTime(){
            currentTS = System.currentTimeMillis();
            long timeElapsed = currentTS - gameStartTS;
            gameTimePassed = (int)(timeElapsed/SECS);
            String time = "";

            if(timeElapsed < SECS){
                time = "3:00";
            } else {
                int mins = 2 - (((int) timeElapsed / (int)SECS) / 60);
                int secs = 59 - (((int) timeElapsed / (int) SECS) % 60);
                time = time.concat(String.valueOf(mins));
                time = time.concat(":");
                time = time.concat(String.valueOf(secs));
            }
            return time;
        }

        private double getAITime(){
            int time = (3*60) - (int)((System.currentTimeMillis() - gameStartTS)/SECS);
            return (double)time;
        }

        private void draw(){
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.text(XDIM - 3.0, YDIM - 0.5, this.gameTime());
        }
    }

    public static long getSecs(){
        return SECS;
    }

    //Communication methods
    //===================================================================================
    public double[] AIstats(int player){
        double[] GBstats = new double[2];
        GBstats[0] = clk.getAITime();
        GBstats[1] = (player==1)?p1.getElixir():p2.getElixir();
        return GBstats;
    }

    public double[] getP1(){
        return p1.getHand();
    }

    public double[] getP2(){
        return p2.getHand();
    }
}
