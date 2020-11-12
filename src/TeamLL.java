/*
Author: Peter Webster
Date: 5/18/20

5/25/2020 UPDATE: added getListBuildings() & getListUnits() methods
 */

import java.awt.*;
import java.io.Serializable;

public class TeamLL implements Serializable {

    private class Node{
        private Node prev, next;
        private Building building;
        private Troop unit;

        private Node(Node prev, Node next, Building building){
            this.prev = prev;
            this.next = next;
            this.building = building;
            this.unit = null;
        }

        private Node(Node prev, Node next, Troop unit){
            this.prev = prev;
            this.next = next;
            this.building = null;
            this.unit = unit;
        }

        private Troop getUnit(){
            return unit;
        }

        private Building getBuilding(){
            return building;
        }
    }

    private Node head, tail;
    private int teamNo, size, buildingCnt, troopCnt;
    private static final Color TEAM1_BLUE = new Color(0, 120, 250);
    private static final Color TEAM2_PURP = new Color(75, 0, 255);

    public TeamLL(int teamNo){
        this.teamNo = teamNo;
        this.size = 0;
        this.buildingCnt = 0;
        this.troopCnt = 0;
    }

    public void addUnit(Troop unit){
        if(size == 0){
            this.head = new Node(null, null, unit);
            this.tail = this.head;
        } else if(size == 1){
            this.tail = new Node(this.head, null, unit);
            this.head.next = this.tail;
        } else {
            this.tail.next = new Node(this.tail, null, unit);
            this.tail = this.tail.next;
        }
        size++;
        troopCnt++;
    }

    public void addBuilding(Building unit){
        if(size == 0){
            this.head = new Node(null, null, unit);
            this.tail = this.head;
        } else if(size == 1){
            this.tail = new Node(this.head, null, unit);
            this.head.next = this.tail;
        } else {
            this.tail.next = new Node(this.tail, null, unit);
            this.tail = this.tail.next;
        }
        size++;
        buildingCnt++;
    }

    private void remove(Node current){
        if(size == 0){
            return;
        } else if(size == 1){
            this.head = null;
            this.tail = null;
        } else if(size == 2){
            //Different cases for removing head or tail
            if(current == this.head){
                this.head = this.tail;
                this.tail.prev = null;
            } else if(current == this.tail){
                this.tail = this.head;
                this.head.next = null;
            }
        } else {
            //Different cases for removing head or tail
            if(current == this.head){
                this.head = current.next;
                this.head.prev = null;
            } else if(current == this.tail){
                this.tail = this.tail.prev;
                this.tail.next = null;
            } else {
                current.prev.next = current.next;
                current.next.prev = current.prev;
            }
        }
        size--;
        if(current.getBuilding() == null){
            troopCnt--;
        } else {
            buildingCnt--;
        }
    }

    //Scans linked list for dead units and buildings
    public void reapDead(){
        Node current = this.head;
        while(current != null){
            //Individual cases for either buildings of troops
            if(current.getUnit() != null){
                if(current.getUnit().getCurrentHP() <= 0){
                    remove(current);
                }
            } else if(current.getBuilding() != null){
                if(current.getBuilding().getCurrentHP() <= 0) {
                    remove(current);
                }
            }
            current = current.next;
        }
    }

    public void sort(GameStructures gs){
        Node current = this.tail;
        boolean swapMade = true;

        while(swapMade){
            swapMade = false;
            while(current != this.head){
                //Check if previous node in list has lower damage potential, if so swap
                //Checks account for false positives due to null values in the wrong type of card being compared
                if(current.getBuilding() == null && current.prev.getBuilding() == null){        //Troop-Troop
                    if(current.getUnit().getDmgPotential(gs) > current.prev.getUnit().getDmgPotential(gs)){
                        swap(current, current.prev);
                        swapMade = true;
                    } else {
                        current = current.prev;
                    }
                } else if(current.getUnit() == null && current.prev.getBuilding() == null) {        //Building-Troop
                    if (current.getBuilding().getDmgPotential() > current.prev.getUnit().getDmgPotential(gs)) {
                        swap(current, current.prev);
                        swapMade = true;
                    } else {
                        current = current.prev;
                    }
                } else if(current.getBuilding() == null && current.prev.getUnit() == null) {        //Troop-Building
                    if (current.getUnit().getDmgPotential(gs) > current.prev.getBuilding().getDmgPotential()) {
                        swap(current, current.prev);
                        swapMade = true;
                    } else {
                        current = current.prev;
                    }
                } else if(current.getUnit() == null && current.prev.getUnit() == null) {        //Building-Building
                    if (current.getBuilding().getDmgPotential() > current.prev.getBuilding().getDmgPotential()) {
                        swap(current, current.prev);
                        swapMade = true;
                    } else {
                        current = current.prev;
                    }
                }
            }
            current = this.tail;
        }
    }

    private void swap(Node greater, Node lesser){
        //Special cases for swapping head & tail
        if(lesser == this.head && greater == this.tail){
            this.head.prev = this.tail;
            this.tail.next = this.head;
            this.tail.prev = null;
            this.head.next = null;
            Node temp = this.head;
            this.head = this.tail;
            this.tail = temp;
        } else if(greater == this.tail){
            lesser.prev.next = greater;
            this.tail = lesser;
            this.tail.next = null;
            greater.prev = lesser.prev;
            greater.next = this.tail;
            this.tail.prev = greater;
        } else if(lesser == this.head){
            greater.next.prev = lesser;
            this.head = greater;
            this.head.prev = null;
            lesser.prev = greater;
            lesser.next = greater.next;
            this.head.next = lesser;
        } else {
            lesser.prev.next = greater;
            greater.next.prev = lesser;
            greater.prev = lesser.prev;
            lesser.next = greater.next;
            greater.next = lesser;
            lesser.prev = greater;
        }
    }

    public String update(GameStructures towers, TeamLL enemy){
        this.reapDead();
        if(size > 1) {
            this.sort(towers);
        }
        String list = "[";
        Node current = this.head;
        while(current != null){
            list = list.concat("[");
            if(current.getBuilding()==null) {
                current.getUnit().update(towers, enemy);
                list = list.concat(String.valueOf(current.getUnit().getID()) + ", ");
                double[] pos = current.getUnit().getPos();
                list = list.concat("[" + String.valueOf(pos[0]) + ", " + String.valueOf(pos[1]) + "]");
            }else if(current.getUnit()==null){
                current.getBuilding().update(towers, enemy);
                list = list.concat(String.valueOf(current.getBuilding().getID()) + ", ");
                double[] pos = current.getBuilding().getPos();
                list = list.concat("[" + String.valueOf(pos[0]) + ", " + String.valueOf(pos[1]) + "]");
            }
            list = list.concat("], ");
            current = current.next;
        }
        //Removes trailing ", "
        if(size > 0) {
            list = list.substring(0, list.length() - 2);
        }
        list = list.concat("]");
        return list;
    }

    public void draw(int teamDrawReq){
        Node current = this.head;
        while(current != null){
            if(current.getBuilding() == null){
                current.getUnit().draw((teamNo==teamDrawReq)?TEAM1_BLUE:TEAM2_PURP, teamDrawReq);
            } else if(current.getUnit() == null){
                current.getBuilding().draw((teamNo==teamDrawReq)?TEAM1_BLUE:TEAM2_PURP, teamDrawReq);
            }
            current = current.next;
        }
//        StdDraw.show();
    }

    // Added for implementation with Troop.aggroMove
    public Building[] getListBuildings(){
        Node current = this.head;
        Building[] cds = new Building[buildingCnt];
        int idx = 0;
        while(current != null){
            if(current.getUnit() == null){
                cds[idx] = current.getBuilding();
                idx++;
            }
            current = current.next;
        }
        return cds;
    }

    // Added for implementation with Troop.aggroMove
    public Troop[] getListUnits(){
        Node current = this.head;
        Troop[] cds = new Troop[troopCnt];
        int idx = 0;
        while(current != null){
            if(current.getBuilding() == null){
                cds[idx] = current.getUnit();
                idx++;
            }
            current = current.next;
        }
        return cds;
    }

    public int getTeamNo() {
        return teamNo;
    }

    public int getSize() {
        return size;
    }

    public double[] AIstats(int player){
        //Create crawler
        Node current = this.head;
        //Create list for 10 cards stats
        double[] teamStats = new double[30];
        for(int i = 0; i < 10; i++){
            double[] cardStat;
            if(i < size) {
                //Get stats
                if (current.getBuilding() != null) {
                    cardStat = current.getBuilding().AIstats(player);
                } else if (current.getUnit() != null) {
                    cardStat = current.getUnit().AIstats(player);
                } else {
                    cardStat = new double[]{0, 0, 0};
                }

                //Advance crawler
                current = current.next;
            } else {
                cardStat = new double[]{0, 0, 0};
            }

            //Load stats into list
            teamStats[3 * i] = cardStat[0];
            teamStats[3 * i + 1] = cardStat[1];
            teamStats[3 * i + 2] = cardStat[2];
        }
        return teamStats;
    }
}
