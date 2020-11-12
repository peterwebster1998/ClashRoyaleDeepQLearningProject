/*
Author: Peter Webster
Date: 5/14/20
 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class TestDriver {
    public static void main(String[] args) {
        Troop c1 = new Troop(1, 3, 1, 0, 0, System.currentTimeMillis(), 1, 1, 100, 100, 1.0, 1.0, 1.5, 9, false);
        Troop c2 = new Troop(1, 2, 2, 0, 0, System.currentTimeMillis(), 1.5, 1, 150, 80, 1.2, 1.0, 4, 9, false);
        Troop c3 = new Troop(1, 3, 3, 0, 0, System.currentTimeMillis(), 1.3, 1.2, 70, 20, 1.4, 1.0, 7, 9, false);
        Building c4 = new Tower(1, 4, 4, 0, 0, System.currentTimeMillis(), 2.5, 2.5, 600, 60, 30, 6, 1.3);
        Troop c5 = new Troop(1, 6, 5, 0, 0, System.currentTimeMillis(), 1, 1, 400, 40, 1.2, 1.0, 5, 9, false);
        Troop c6 = new Troop(1, 1, 6, 0, 0, System.currentTimeMillis(), 2, 1.5, 300, 50, 1.0, 1.0, 3, 9, true);
        Troop c7 = new Troop(1, 4, 7, 0, 0, System.currentTimeMillis(), 3, 1, 1000, 150, 0.5, 1.0, 2, 9, true);
        Troop c8 = new SplashTroop(1, 4, 8, 0, 0, System.currentTimeMillis(), 1.75, 1.5, 700, 75, 0.9, 1.0, 0.5, 9, false, 1.5);

        TeamLL plyr1 = new TeamLL(1);
        TeamLL plyr2 = new TeamLL(2);

        Deck d1, d2;

        try {
            d1 = new Deck(1, new int[]{1, 2, 3, 4, 5, 6, 7, 8}, plyr1);
            d2 = new Deck(2, new int[]{1, 2, 3, 4, 5, 6, 7, 8}, plyr2);


//
//        System.out.println(d1.toString());
//        for(int i = 0; i < 8; ++i){
//            Card card = d1.playCard((i%4)+1);
//            System.out.println("Card played ID: " + card.getID());
//            System.out.println(d1.toString());
//        }

//        System.out.println("\n\n====================================\nTesting deck shuffle:");
//        long start = System.currentTimeMillis();
            d1.shuffle(25);
//        long end = System.currentTimeMillis();
//        System.out.println(d1.toString());
//        System.out.println("Shuffle time = " + (end - start));

            // Test of structural components of game
            GameBoard g = new GameBoard(d1, d2);
            GameStructures gs = new GameStructures();
            g.draw(1);


            while (gs.gameWon().equals("Game Still Going...")) {
                //Check for move
                if (StdDraw.isKeyPressed(KeyEvent.VK_1)) {
                    d1.setCardSelected(d1.getC1());
                } else if (StdDraw.isKeyPressed(KeyEvent.VK_2)) {
                    d1.setCardSelected(d1.getC2());
                } else if (StdDraw.isKeyPressed(KeyEvent.VK_3)) {
                    d1.setCardSelected(d1.getC3());
                } else if (StdDraw.isKeyPressed(KeyEvent.VK_4)) {
                    d1.setCardSelected(d1.getC4());
                }
                if (StdDraw.isMousePressed()) {
                    d1.playFromHand(StdDraw.mouseX(), StdDraw.mouseY(), plyr2, gs);
                    System.out.println(StdDraw.mouseX() + ", " + StdDraw.mouseY());
                }
                //Background Calculations
                gs.updateStates(1);
                gs.updateTowers(plyr1, plyr2);
                plyr1.update(gs, plyr2);
                g.draw(1);
                gs.draw(1);
                plyr1.draw(1);
                StdDraw.show();
            }

            gs.gameWinBanner(gs.gameWon());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
