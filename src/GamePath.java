/*
Author: Peter Webster
Date: 5/17/20

5/22/20 UPDATE: added onPath & getInstruction methods
 */

import java.awt.*;

public final class GamePath {

    private static final double XDIM = 18.0;
    private static final double YDIM = 32.0;
    private static final Color PATH_BROWN = new Color(200, 100, 0);
    private double[] Xcoords;
    private double[] Ycoords;

    public GamePath(){
        this.Xcoords = new double[]{7.5, XDIM - 7.5, XDIM - 7.5, XDIM - 3.5, XDIM - 3.5, XDIM - 7.5, XDIM - 7.5, 7.5, 7.5, 3.5, 3.5, 7.5, 7.5};
        this.Ycoords = new double[]{1.5, 1.5, 3.5, 3.5, YDIM - 3.5, YDIM - 3.5, YDIM - 1.5, YDIM - 1.5, YDIM - 3.5, YDIM - 3.5, 3.5, 3.5, 1.5};
    }

    public void draw(){
        StdDraw.setPenColor(PATH_BROWN);
        StdDraw.setPenRadius(0.05);
        StdDraw.polygon(Xcoords, Ycoords);
    }

    public static boolean onPath(double x, double y){
        if((((y >= 1 && y <= 2) || (y >= YDIM-2 && y <= YDIM-1)) && x >= 7 && x <= XDIM-7) ||   //bottom and top horizontals
                (((y >= 1 && y <= 4) || (y >= YDIM-4 && y <= YDIM-1)) && ((x >= 7 && x <= 8) || (x >= XDIM-8 && x <= XDIM-7))) ||   //verticals off bottom and top horizontals
                (((y >= 3 && y <= 4) || (y >= YDIM-4 && y <= YDIM-3)) && ((x >= 3 && x <= 8) || (x >= XDIM-8 && x <= XDIM-3))) ||   //next level horizontals
                ((y >= 3 && y <= YDIM-3) && ((x >= 3 && x <= 4) || (x >= XDIM-4 && x <= XDIM-3)))){ //main verticals
            return true;
        } else {
            return false;
        }
    }

    public static String getInstruction(double x, double y){
        if((y >= 1 && y <= 2 && x >= 8 && x <= XDIM/2) || (y >= YDIM-2 && y <= YDIM-1 && x >= XDIM/2 && x <= XDIM-8) ||
                (y >= 3 && y <= 4 && x >= 4 && x <= 8) || (y >= YDIM-4 && y <= YDIM-3 && x >= XDIM-8 && x <= XDIM-3)){
            return "left";
        } else if((y >= YDIM-2 && y <= YDIM-1 && x >= 7 && x <= XDIM/2) || (y >= 1 && y <= 2 && x >= XDIM/2 && x <= XDIM-8) ||
                (y >= YDIM-4 && y <= YDIM-3 && x >= 3 && x <= 7) || (y >= 3 && y <= 4 && x >= XDIM-8 && x <= XDIM-4)){
            return "right";
        } else if((((y >= 1 && y <= 3) || (y >= YDIM-4 && y <= YDIM-2)) && ((x >= 7 && x <= 8) || (x >= XDIM-8 && x <= XDIM-7)))
                || (y >= 3 && y <= YDIM-4 && ((x >= 3 && x <= 4) || (x >= XDIM-4 && x <= XDIM-3 )))){
            return "up";
        }
        return "not on path";
    }
}

