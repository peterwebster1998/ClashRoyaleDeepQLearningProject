import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
Author: Peter Webster
Date: 5/30/20
 */
public class ServerDriver {
    public static void main(String[] args) {
        int numPlayers = 2;

        //Thread[] ths = new Thread[numPlayers];

        try {
            ServerSocket ss = new ServerSocket(7000);
            Socket[] s = new Socket[numPlayers];
            ObjectInputStream[] ois = new ObjectInputStream[numPlayers];
            ObjectOutputStream[] oos = new ObjectOutputStream[numPlayers];

            //Wait for players to join
            for(int i = 0; i < numPlayers; i++) {
                System.out.println("Waiting for a call");
                s[i] = ss.accept();  // blocking
                System.out.println("Player " + (i+1) + " has entered the game");

                ois[i] = new ObjectInputStream(s[i].getInputStream());
                oos[i] = new ObjectOutputStream(s[i].getOutputStream());

                //Tell client which player they are
                String player = "Player ";
                player = player.concat(String.valueOf(i+1));
                oos[i].writeObject(player);
            }

            System.out.println("All players connected");

            //Indicators of playing again
            boolean[] playAgain = new boolean[numPlayers];
            playAgain[0] = true;
            playAgain[1] = true;

            while(playAgain[0] && playAgain[1]){
                //Wait till players are ready
                while(playAgain[0] || playAgain[1]){
                    for(int i = 0; i < numPlayers; i++){
                        if(playAgain[i]) {
                            String ready = (String)ois[i].readObject();
                            if (ready.equals("Y") || ready.equals("y")) {
                                playAgain[i] = false;
                            }
                        }
                    }
                }

                //Instantiate Game Components
                TeamLL[] players = new TeamLL[numPlayers];
                for(int i = 0; i < numPlayers; i++){
                    players[i] = new TeamLL(i+1);
                }

                //Create Decks
                Deck[] decks = new Deck[numPlayers];

                //Receive deck choice
                for(int i = 0; i < numPlayers; i++) {
                    //Instantiate deck
                    int[] deckSelection = (int[])ois[i].readObject();
                    decks[i] = new Deck(i+1, deckSelection, players[i]);   //Args: teamNo, IDlist, teamLL
                    //Shuffle deck
                    decks[i].shuffle(25);
                }

                //Instantiate board & Towers
                GameBoard gb = new GameBoard(decks[0], decks[1]);
                GameStructures gs = new GameStructures();

                //Send GameStructure & GameBoard to players
                for(int i = 0; i < numPlayers; i++){
                    oos[i].writeObject(gb);
                    oos[i].writeObject(gs);
                }

                while(gs.gameWon().equals("Game Still Going...")){
                    for(int i = 0; i < numPlayers; i++){
                        //Receive player instructions
                        int cardChoice = ois[i].readInt();
                        Double[] xy = (Double[])ois[i].readObject();
                        CommunicationHandler.interpret(i+1, cardChoice, xy, decks[i], gs, (i==0)?players[1]:players[0]);
                    }

                    //Update Game State
                    gs.updateStates(1);
                    gs.updateTowers(players[0], players[1]);
                    players[0].update(gs, players[1]);
                    players[1].update(gs, players[0]);

//                    //Send Updated State to Network
//                    for(int i = 0; i < numPlayers; i++){
//                        // Sends double[68]
//                        oos[i].writeObject(CommunicationHandler.createComm(i+1, gs, players, gb));
//                    }

                    //Send Gamestates
                    for(int i = 0; i < numPlayers; i++) {
                        oos[i].writeObject(gb);
                    }for(int i = 0; i < numPlayers; i++) {
                        oos[i].writeObject(gs);
                    }for(int i = 0; i < numPlayers; i++) {
                        oos[i].writeObject((i == 0) ? players[0] : players[1]);
                    }for(int i = 0; i < numPlayers; i++){
                        oos[i].writeObject((i==0)?players[1]:players[0]);
                    }

                    //Draw Current Game State
                    gb.draw(1);
                    gs.draw(1);
                    players[0].draw(1);
                    players[1].draw(1);
                    StdDraw.show();
                }

                //Display Winner
                gs.gameWinBanner(gs.gameWon());

                //Check to see if players want to play again
                for(int i = 0; i < numPlayers; i++){
                    playAgain[i] = ois[i].readBoolean();
                }
            }

            ss.close();
            for(int i = 0; i < 2; i++){
                oos[i].close();
                ois[i].close();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

