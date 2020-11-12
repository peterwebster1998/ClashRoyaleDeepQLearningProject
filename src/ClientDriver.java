import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Scanner;

/*
Author: Peter Webster
Date: 5/30/20
 */
public class ClientDriver {
    public static void main(String[] args) {
        try {
            //Connect to server
            System.out.println("About to call");
            Socket s = new Socket("192.168.0.87", 7000);
            System.out.println("Connected");

            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

            //Instantiate scanner to receive player input
            Scanner in = new Scanner(System.in);

            //Find our playerNo
            String player = (String)ois.readObject();
            int teamNo = Integer.parseInt(player.split(" ")[1]);

            boolean playAgain = true;

            while(playAgain) {
                while(playAgain) {
                    System.out.print("Type 'Y' when ready: ");
                    String response = in.nextLine();
                    if(response.equals("y") || response.equals("Y")){
                        oos.writeObject(response);
                        playAgain = false;
                    }
                }

                //Enter deck choice
                int[] deckIDs = new int[8];
                int deckSize = 0;
                while (deckSize < 8) {
                    System.out.print("\nPlease enter card " + (deckSize + 1) + " ID: ");
                    int ID = in.nextInt();

                    //Check if already in deck
                    boolean contains = false;
                    for (int i = 0; i < deckSize; i++) {
                        if (ID == deckIDs[i]) {
                            contains = true;
                        }
                    }

                    //If false add to deck
                    if (!contains) {
                        deckIDs[deckSize] = ID;
                        deckSize++;
                    }
                }

                //Send Deck
                oos.writeObject(deckIDs);

                //Create Client Side game structures
                GameBoard gb = (GameBoard) ois.readObject();
                GameStructures gs = (GameStructures) ois.readObject();

                //Entering Game:
                //Drawing Screen:
                //Enable Double Buffering
                StdDraw.enableDoubleBuffering();
                //Set canvas size and Scale
                StdDraw.setCanvasSize(550, 1000);
                StdDraw.setXscale(0.0, GameBoard.getXdim());
                StdDraw.setYscale(-(GameBoard.getYdim())/9, GameBoard.getYdim());


                while (gs.gameWon().equals("Game Still Going...")) {
                    //Check for move
                    boolean cardSelected = false;

                    //Send Handshake to server
//                oos.writeBoolean(true);
                    //Check for keypresses
                    if (StdDraw.isKeyPressed(KeyEvent.VK_1)) {
                        oos.writeInt(1);
                        cardSelected = true;
                    } else if (StdDraw.isKeyPressed(KeyEvent.VK_2)) {
                        oos.writeInt(2);
                        cardSelected = true;
                    } else if (StdDraw.isKeyPressed(KeyEvent.VK_3)) {
                        oos.writeInt(3);
                        cardSelected = true;
                    } else if (StdDraw.isKeyPressed(KeyEvent.VK_4)) {
                        oos.writeInt(4);
                        cardSelected = true;
                    } else {
                        oos.writeInt(0);
                        cardSelected = true;
                    }

                    //Check for mouse click
                    if (StdDraw.isMousePressed() && cardSelected) {
                        Double[] pos = new Double[2];
                        pos[0] = StdDraw.mouseX();
                        pos[1] = StdDraw.mouseY();
                        oos.writeObject(pos);
                        cardSelected = false;
                        System.out.println(StdDraw.mouseX() + ", " + StdDraw.mouseY());
                    } else {
                        Double[] pos = new Double[2];
                        pos[0] = 42.0;
                        pos[1] = 2020.0;
                        oos.writeObject(pos);
                    }

                    //Recieve Updated Game Pieces
                    gb = (GameBoard) ois.readObject();
                    gs = (GameStructures) ois.readObject();
                    TeamLL me = (TeamLL) ois.readObject();
                    TeamLL them = (TeamLL) ois.readObject();

                    if(me.getSize() > 0 || them.getSize() > 0){
                        System.out.println("Oh hey!");
                    }

                    gb.draw(teamNo);
                    gs.draw(teamNo);
                    me.draw(teamNo);
                    them.draw(teamNo);

                    StdDraw.show();

                }
            }


            s.close();
            oos.close();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
