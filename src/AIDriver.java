import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.*;

public class AIDriver {
    public static void main(String[] args) {
        int numPlayers = 2;

        //Thread[] ths = new Thread[numPlayers];

        try {
            ServerSocket ss = new ServerSocket(7000);
            Socket[] s = new Socket[numPlayers];
            BufferedReader[] in = new BufferedReader[numPlayers];
            PrintWriter[] out = new PrintWriter[numPlayers];

            //BufferedReader in =  new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
            //Wait for players to join
            for (int i = 0; i < numPlayers; i++) {
                System.out.println("Waiting for a call");
                s[i] = ss.accept();  // blocking
                System.out.println("Player " + (i + 1) + " has entered the game");

                in[i] = new BufferedReader(new InputStreamReader(s[i].getInputStream()));
                out[i] = new PrintWriter(s[i].getOutputStream(), true);

                //Send playerNo
//                out[i].println(i+1);
            }

            System.out.println("All players connected");

            //Indicators of playing again
            boolean[] playAgain = new boolean[numPlayers];
            playAgain[0] = true;
            playAgain[1] = true;

            while (playAgain[0] && playAgain[1]) {
                playAgain[0] = false;
                playAgain[1] = false;

                //Instantiate Game Components
                TeamLL[] players = new TeamLL[numPlayers];
                for (int i = 0; i < numPlayers; i++) {
                    players[i] = new TeamLL(i + 1);
                }

                //Create Decks
                Deck[] decks = new Deck[numPlayers];
                for (int i = 0; i < numPlayers; i++) {
                    //Instantiate deck
                    int[] deckSelection = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
                    decks[i] = new Deck(i + 1, deckSelection, players[i]);   //Args: teamNo, IDlist, teamLL
                    //Shuffle deck
                    decks[i].shuffle(25);
                }

                //Instantiate board & Towers
                GameBoard gb = new GameBoard(decks[0], decks[1]);
                GameStructures gs = new GameStructures();

                //Alert Players of game start
                for(int i = 0; i < numPlayers; i++){
                    out[i].println(1);
                }

                //Update frequency control variables
                long lastUpdateTS = System.currentTimeMillis();

                while (gs.gameWon().equals("Game Still Going...")) {
                    //Update Game State
                    gs.updateStates(1);
                    gs.updateTowers(players[0], players[1]);
                    players[0].update(gs, players[1]);
                    players[1].update(gs, players[0]);

                    //Draw Current Game State
                    gb.draw(1);
                    gs.draw(1);
                    players[0].draw(1);
                    players[1].draw(1);
                    StdDraw.show();

                    // if greater than 0.1 secs has passed since last play, get next play from network
                    if((System.currentTimeMillis() - lastUpdateTS) / GameBoard.getSecs() > 0.1){

                        lastUpdateTS = System.currentTimeMillis();
                        //Send one to inform network game is still going
                        for (int i = 0; i < numPlayers; i++) {
                            out[i].println(1);
                        }

                        //Send Updated State to Network
                        for (int i = 0; i < numPlayers; i++) {
                            // Sends double[73]
                            double[] gameState = CommunicationHandler.AIencode(i + 1, players[0], players[1], gs, gb);
                            out[i].println(new JSONArray(gameState).toString());
                        }

                        for (int i = 0; i < numPlayers; i++) {
                            //Receive instructions from player networks
                            String instructions = in[i].readLine();
                            //System.out.println("Recieved String: " + instructions);
                            String instr = instructions.substring(1, instructions.length() - 1);
                            //System.out.println("Formatted String: " + instr);
                            String[] ins = instr.split(", ");
                            double[] inst = new double[3];
                            //Convert JSONArray to double[]
                            for (int j = 0; j < ins.length; j++) {
                                inst[j] = Double.parseDouble(ins[j]);
                            }
                            //System.out.println("Converted to double[]");
                            CommunicationHandler.AIdecode(i + 1, inst, decks[i], gs, (i == 0) ? players[1] : players[0]);
                        }
                    }
                }
                // Send zero to alert network to end of game
                for(int i = 0; i < numPlayers; i++){
                    out[i].println(0);
                }

                //Send Game results to networks
                for (int i = 0; i < numPlayers; i++) {
                    double[] results = CommunicationHandler.sendResults(i + 1, gs);
                    out[i].println(new JSONArray(results).toString());
                }

                //Check play again
                for (int i = 0; i < numPlayers; i++) {
                    String response = in[i].readLine();
                    playAgain[i] = ( Integer.parseInt(response) == 1);
                }
            }
            ss.close();
            for(int i = 0; i < 2; i++){
                out[i].close();
                in[i].close();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}