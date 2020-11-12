/*
Author: Peter Webster
Date: 5/13/20

5/18/2020 UPDATE: Added Draw Method
5/31/2020 UPDATE: Reworked Class, added fetchCardData() method, reworked playFromHand() method
 */

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

public class Deck implements Serializable {

    //Required for draw method
    private static final double XDIM = 18.0;
    private static final double YDIM = 32.0;

    private class Node implements Serializable{

        private Node prev, next;
        private int cardID;

        private Node(int card){
            this.cardID = card;
        }

        private Node(int card, Node prev, Node next){
            this.cardID = card;
            this.next = next;
            this.prev = prev;
        }

        private int getCard(){
            return cardID;
        }

        private Node getPrev(){
            return prev;
        }

        private Node getNext(){
            return next;
        }

        private void setPrev(Node n){
            this.prev = n;
        }

        private void setNext(Node n){
            this.next = n;
        }
    }

    private Node c1, c2, c3, c4, end;
    private int cardSelected = 0;
    private TeamLL myTeam;
    private int teamNo;
    private int[] deckIDs;
    private String[] cardNames = new String[8];
    private double[][] cardStats = new double[8][14];
    private double elixir = 0.0;
    private long gameStartTS, updateTS;

    public Deck(int teamNo, int[] deckIDs, TeamLL myTeam) throws IOException {
        //Instantiate Variables
        this.myTeam = myTeam;
        this.teamNo = teamNo;
        this.deckIDs = deckIDs;

        //Collect card data
        this.fetchCardData();

        // Link deck together
        this.c1 = new Node(deckIDs[0]);
        this.c2 = new Node(deckIDs[1], this.c1, null);
        this.c3 = new Node(deckIDs[2], this.c2, null);
        this.c4 = new Node(deckIDs[3], this.c3, null);
        this.c4.next = new Node(deckIDs[4], this.c4, null);
        this.c4.next.next = new Node(deckIDs[5], this.c4.next, null);
        this.c4.next.next.next = new Node(deckIDs[6], this.c4.next.next, null);
        this.end = new Node(deckIDs[7], this.c4.next.next.next, null);

        this.c1.next = this.c2;
        this.c2.next = this.c3;
        this.c3.next = this.c4;
        this.c4.next.next.next.next = this.end;
    }

    //fetches all the stats for the cards from a csv file
    public void fetchCardData() throws IOException {
        String cardData = "/home/peter/Documents/Machine Learning/Final Project/ClashRoyaleCardInfo.csv";
        String line = "";
        String csvSplit = ",";
        int dataFetched = 0;

        //while data for all cards hasn't been collected
        while(dataFetched < 8) {
            //Open file to read
            BufferedReader br = new BufferedReader(new FileReader(cardData));

            //Remove Column Titles
            br.readLine();
            boolean dataFiled = false;

            //Read through file
            while ((line = br.readLine()) != null && !dataFiled) {
                String[] output = line.split(csvSplit);
                //Check if ID matches card
                if(Double.parseDouble(output[0]) == deckIDs[dataFetched]){
                    for(int i = 0; i < output.length; i++){
                        if(i != 2) {
                            //Save data in cardStats
                            if(i == 12){
                                //Save boolean flying as 1 or 0
                                cardStats[dataFetched][(i < 2) ? i : i - 1] = (Boolean.parseBoolean(output[i])) ? 1.0 : 0.0;
                            } else {
                                cardStats[dataFetched][(i < 2) ? i : i - 1] = Double.parseDouble(output[i]);
                            }
                        } else {
                            //Save name in cardNames
                            cardNames[dataFetched] = output[i];
                        }
                    }
                    dataFiled = true;
                    dataFetched++;
                }
            }
        }
        System.out.println("Player " + teamNo + " deck data collected");
    }

    public int getCardSelected(){
        return cardSelected;
    }

    public int getC1(){
        return c1.getCard();
    }

    public int getC2(){
        return c2.getCard();
    }

    public int getC3(){
        return c3.getCard();
    }

    public int getC4(){
        return c4.getCard();
    }

    public void setCardSelected(int id){
        this.cardSelected = id;
    }

    public int playCard(int cardNo){

        int play = 0;

        switch(cardNo){
            case 1:
//                System.out.println("Case 1:");
                // Save card to be played
                play = this.c1.cardID;

                //Move card played to end of deck
                this.c1.prev = this.end;
                this.end.next = this.c1;
                this.end = this.c1;

                //Shift Cards up
                this.c1 = this.c4.next;
                this.c4.next = this.c4.next.next;
                this.c4.next.prev = this.c4;
                this.c2.prev = this.c1;
                this.c1.next = this.c2;
                this.c1.prev = null;

                //Make card played end of list
                this.end.next = null;

                break;

            case 2:
//                System.out.println("Case 2:");
                // Save card to be played
                play = this.c2.cardID;

                //Move card played to end of deck
                this.c2.prev = this.end;
                this.end.next = this.c2;
                this.end = this.c2;

                //Shift Cards up
                this.c2 = this.c4.next;
                this.c4.next = this.c4.next.next;
                this.c4.next.prev = this.c4;
                this.c3.prev = this.c2;
                this.c2.next = this.c3;
                this.c2.prev = this.c1;
                this.c1.next = this.c2;

                //Make card played end of list
                this.end.next = null;

                break;

            case 3:
//                System.out.println("Case 3:");
                // Save card to be played
                play = this.c3.cardID;

                //Move card played to end of deck
                this.c3.prev = this.end;
                this.end.next = this.c3;
                this.end = this.c3;

                //Shift Cards up
                this.c3 = this.c4.next;
                this.c4.next = this.c4.next.next;
                this.c4.next.prev = this.c4;
                this.c4.prev = this.c3;
                this.c3.next = this.c4;
                this.c3.prev = this.c2;
                this.c2.next = this.c3;

                //Make card played end of list
                this.end.next = null;

                break;

            case 4:
//                System.out.println("Case 4:");
                // Save card to be played
                play = this.c4.cardID;

                //Move card played to end of deck
                this.c4.prev = this.end;
                this.end.next = this.c4;
                this.end = this.c4;

                //Shift Cards up
                this.c4 = this.c4.next;
                this.c3.next = this.c4;
                this.c4.prev = this.c3;

                //Make card played end of list
                this.end.next = null;

                break;
        }

        return play;
    }

    public void shuffle(int shuffles){
        Random rng = new Random();

        // PLays a given number of cards in a random order to shuffle the deck before each game
        for(int i = 0; i < shuffles; ++i){
            int cardNo = (rng.nextInt() % 4) + 1;   //+1 Shifts 0-3 to 1-4
            this.playCard(cardNo);
        }
    }

    public String toString(){
        Node current = this.c1;
        String out = "[";
        int cardID = current.getCard();

        while(current.next != null){
            out = out.concat(String.valueOf(cardID));
            out = out.concat(", ");
            current = current.next;
            cardID = current.getCard();
        }

        out = out.concat(String.valueOf(cardID));
        out = out.concat("]");
        return out;
    }

    public void draw(){
        Node current = this.c1;
        //Draw current playable hand
        for(int i = 0; i < 4; i++){
            if(current.getCard() == cardSelected){
                //Highlight Selected Card
                StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
                StdDraw.filledRectangle(3 + (0.5*3.75) + (i*3.75), -0.5*(YDIM/9), 0.5*3.75, 0.5*(YDIM/9));
            }
            //Create filepath for image
            String filepath = "/home/peter/Documents/Machine Learning/Final Project/CardPics/";
            filepath = filepath.concat(String.valueOf(current.cardID));
            filepath = filepath.concat(".png");
            StdDraw.picture(3 + (0.5*3.75) + (i*3.75), -0.5*(YDIM/9), filepath, 0.9*3.75, 0.9*(YDIM/9));
            current = current.next;
        }

        //Create filepath for image
        String filepath = "/home/peter/Documents/Machine Learning/Final Project/CardPics/";
        filepath = filepath.concat(String.valueOf(current.cardID));
        filepath = filepath.concat(".png");
        //Draw next card in line to hand
        StdDraw.picture(1.5, -0.5*(YDIM/9), filepath,  0.7*3, 0.7*(YDIM/9));

        //Draw Elixir
        drawElixir();

    }

    public void playFromHand(double mouseX, double mouseY, TeamLL enemy, GameStructures gs) {
        int statIdx = 0;
        //check there is a card selected
        if(cardSelected==0){return;}

        //Find selected card stats
        while(statIdx < 8 && (int)cardStats[statIdx][0]!=cardSelected){
            statIdx++;
        }

        if(statIdx != 8 && cardStats[statIdx][3] <= this.getElixir()) {
            //Place that card into a team
            switch ((int) cardStats[statIdx][1]) {
                //Case 1 - Troop
                case 1:
                    myTeam.addUnit((Troop) new Troop(teamNo, (int) cardStats[statIdx][2], (int) cardStats[statIdx][0], mouseX, mouseY, System.currentTimeMillis(), cardStats[statIdx][3], cardStats[statIdx][4],
                            (int) cardStats[statIdx][5], (int) cardStats[statIdx][6], cardStats[statIdx][7], cardStats[statIdx][8], cardStats[statIdx][9], cardStats[statIdx][10], (cardStats[statIdx][11] == 1.0) ? true : false));
                    break;
                //Case 2 - SplashTroop
                case 2:
                    myTeam.addUnit((Troop) new SplashTroop(teamNo, (int) cardStats[statIdx][2], (int) cardStats[statIdx][0], mouseX, mouseY, System.currentTimeMillis(), cardStats[statIdx][3], cardStats[statIdx][4],
                            (int) cardStats[statIdx][5], (int) cardStats[statIdx][6], cardStats[statIdx][7], cardStats[statIdx][8], cardStats[statIdx][9], cardStats[statIdx][10], (cardStats[statIdx][11] == 1.0) ? true : false,
                            cardStats[statIdx][12]));
                    break;
                //Case 3 - Tower
                case 3:
                    myTeam.addBuilding((Building) new Tower(teamNo, (int) cardStats[statIdx][2], (int) cardStats[statIdx][0], mouseX, mouseY, System.currentTimeMillis(), cardStats[statIdx][3], cardStats[statIdx][4],
                            (int) cardStats[statIdx][5], (int) cardStats[statIdx][6],
                            (int) cardStats[statIdx][7], (int) cardStats[statIdx][8], (int) cardStats[statIdx][9]));
                    break;
                //Case 4 - Spawner
                case 4:
                    myTeam.addBuilding((Building) new Spawner(teamNo, (int) cardStats[statIdx][2], (int) cardStats[statIdx][0], mouseX, mouseY, System.currentTimeMillis(), cardStats[statIdx][3], cardStats[statIdx][4],
                            (int) cardStats[statIdx][5], (int) cardStats[statIdx][6],
                            (int) cardStats[statIdx][7], (int) cardStats[statIdx][8]));
                    break;
                //Case 5 - Spell
                case 5:
                    Spell temp = new Spell(teamNo, (int) cardStats[statIdx][2], (int) cardStats[statIdx][0], mouseX, mouseY, System.currentTimeMillis(), cardStats[statIdx][3], cardStats[statIdx][4],
                            (int) cardStats[statIdx][5], cardStats[statIdx][6], cardStats[statIdx][7], (int) cardStats[statIdx][8]);
                    temp.attack(enemy, gs);
                    break;
            }

            //play card from hand
            if (cardSelected == this.getC1()) {
                this.playCard(1);
            } else if (cardSelected == this.getC2()) {
                this.playCard(2);
            } else if (cardSelected == this.getC3()) {
                this.playCard(3);
            } else if (cardSelected == this.getC4()) {
                this.playCard(4);
            }

            //Deselect card and spend elixir
            cardSelected = 0;
            this.useElixir((int)cardStats[statIdx][3]);
        }
    }

    public void gameStart(){
        shuffle(25);
        gameStartTS = System.currentTimeMillis();
        elixirStart();
    }

    public void elixirStart(){
        elixir = 6.0;
        updateTS = System.currentTimeMillis();
    }

    public int getElixir(){
        long currentTS = System.currentTimeMillis();
        double timePast = (double)(currentTS-updateTS)/GameBoard.getSecs();
        if(GameBoard.getGameTimePassed() < 120) {
//            System.out.println("Single Elixir - Time Passed = " + GameBoard.getGameTimePassed());
//            elixir += timePast / 2.8;
            elixir += timePast / 10;
        } else if (GameBoard.getGameTimePassed() >= 120 && GameBoard.getGameTimePassed() < 240){
//            System.out.println("Double Elixir - Time Passed = " + GameBoard.getGameTimePassed());
            elixir += timePast / 1.4;
        } else {
//            System.out.println("Triple Elixir - Time Passed = " + GameBoard.getGameTimePassed());
            elixir += timePast / 0.9;
        }
        elixir = (elixir>10)?10:elixir;
        updateTS = currentTS;
        return (int)elixir;
    }

    public void useElixir(int cost){
        getElixir();
        elixir -= cost;
        updateTS = System.currentTimeMillis();
    }

    public void drawElixir(){
        StdDraw.setPenColor(Color.RED);
        StdDraw.text(3.0, (teamNo==1)?0.5:GameBoard.getYdim()-0.5, String.valueOf((int)elixir));
    }

    public double[] getHand(){
        double[] hand = new double[5];
        hand[0] = c1.cardID;
        hand[1] = c2.cardID;
        hand[2] = c3.cardID;
        hand[3] = c4.cardID;
        hand[4] = c4.next.cardID;
        return hand;
    }
}

