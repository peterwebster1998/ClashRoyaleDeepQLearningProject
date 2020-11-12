import java.io.ObjectOutputStream;

public class CommunicationHandler {

    public static void interpret(int teamNo, int cardSelection, Double[] pos, Deck d, GameStructures gs, TeamLL enemy){
        //Update card choice
        if(cardSelection > 0 && cardSelection < 5){
            switch(cardSelection){
                case 1:
                    d.setCardSelected(d.getC1());
                    System.out.println(d.getCardSelected() + ": Selected.");
                    break;
                case 2:
                    d.setCardSelected(d.getC2());
                    System.out.println(d.getCardSelected() + ": Selected.");
                    break;
                case 3:
                    d.setCardSelected(d.getC3());
                    System.out.println(d.getCardSelected() + ": Selected.");
                    break;
                case 4:
                    d.setCardSelected(d.getC4());
                    System.out.println(d.getCardSelected() + ": Selected.");
                    break;
            }
        }
        //Check TeamNo
        if(!(pos[0] == 42.0 && pos[1] == 2020.0)) {
            //Make Placement Legal
            double[] xy = gs.legalPlacement(pos, teamNo);
            d.playFromHand(xy[0], xy[1], enemy, gs);
        }
    }

    public static double[] AIencode(int player, TeamLL plyr1, TeamLL plyr2, GameStructures gs, GameBoard gb){
        double[] hand = (player==1)?gb.getP1():gb.getP2();
        double[] timeANDelixir = gb.AIstats(player);
        double[][] towerHealths = gs.AIstats(player);
        double[] enemyTeam = (player==1)?plyr2.AIstats(player):plyr1.AIstats(player);
        double[] friendlyTeam = (player==1)?plyr1.AIstats(player):plyr2.AIstats(player);

        double[] msg = new double[73];

        for(int i = 0; i < msg.length; i++){
            //Hand
            if(i < 5){
                msg[i] = hand[i];
            //Time & Elixir
            } else if(i < 7){
                msg[i] = timeANDelixir[i-5];
            //Enemy Tower Healths
            } else if(i < 10){
                msg[i] = towerHealths[(player==1)?1:0][i-7];
            //Enemy Team State
            } else if(i < 40){
                msg[i] = enemyTeam[i-10];
            //Player's Tower Health
            } else if(i < 43){
                msg[i] = towerHealths[(player==1)?0:1][i-40];
            //Player's Team State
            } else {
                msg[i] = friendlyTeam[i-43];
            }
        }
        return msg;
    }

    public static void AIdecode(int player, double[] msg, Deck d, GameStructures gs, TeamLL enemy){
        double card = msg[0];
        Double[] pos = new Double[2];
        pos[0] = (Double)msg[1];
        pos[1] = (Double)msg[2];
        //Send to game
        interpret(player, (int)card, pos, d, gs, enemy);
    }

    public static double[] sendResults(int player, GameStructures gs){
        double[] towerStates = new double[6];
        double[][] AIstats = gs.AIstats(player);
        //Enemy Tower health then Players
        for(int i = 0; i < 6; i++){
                towerStates[i] = AIstats[(i<3)?1:0][(i<3)?i:i-3];
        }
        return towerStates;
    }

}

