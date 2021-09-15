package src2.checkers;

import javax.swing.JPanel;
import src2.App;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;

public class checkers extends JPanel{
    
    public static int blocksize = 37;
    int h = 300;
    int w = 300; // h and w of the gameboard
    int n = 8; // number of columns and rows
    public boolean yourTurn = false;
    stone movingStone;
    ArrayList<stone> possibleKills = new ArrayList<stone>();

    String gameState; // SI SCRIVE PRIMA QUELLI DELL'AVVERSARIO

    App gameroom;

    ArrayList<stone> pos = new ArrayList<stone>();
    ArrayList<stone> oppPos = new ArrayList<stone>();
    ArrayList<stone> moveDot = new ArrayList<stone>();

    public checkers(boolean turn, App p){
        setSize(500,500);
        setVisible(true);
        yourTurn = turn;
        gameroom = p;

        resetGrid();
    }

    //===================================================================================================== ME MOVING ======================================================

    public void nextMove(stone dot, stone s){

        pos.remove(s);
        stone ss = new stone(dot.x-12, dot.y-12);
        ss.damone = checkBecomeDamone(ss, s);
        pos.add(ss);
        moveDot.clear();
        yourTurn = false;
        
        for (stone pk : possibleKills){ //killing
            if (pk.x == dot.x+37-12 || pk.x == dot.x-37-12) oppPos.remove(pk);
        }
        repaint();

        gameState = createGameState();
        try {
            if (checkWin()) {System.out.println("hi :)"); gameroom.checkersLastTurn(gameState+"\n");}
            else gameroom.checkersNextTurn(gameState+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkWin();
    }

    public boolean validateMove(double x, double y){

        stone s = new stone(Math.floor(x)*blocksize, Math.floor(y)*blocksize);
        stone dot = new stone((Math.floor(x)*blocksize)+12, (Math.floor(y)*blocksize)+12, true);
        if(pos.contains(s)) {
            s.damone = checkDamone(pos, s);
            calculatePossibleMoves(s);
            movingStone = s;
            return true;
        } else if (moveDot.contains(dot)) {
            nextMove(dot, movingStone);
            return true;
        }
        else return false;
    }

    public void calculatePossibleMoves(stone s){

        moveDot.clear();
        possibleKills.clear();
        stone ss = new stone((Math.floor(s.x/blocksize)-1)*blocksize, (Math.floor(s.y/blocksize)-1)*blocksize);//tl
        if(!pos.contains(ss) && !oppPos.contains(ss)) {
            stone dot1 = new stone(ss.x, ss.y, true);
            moveDot.add(dot1);
        } else if (!pos.contains(ss) && oppPos.contains(ss)) {
            possibleKills.add(ss);
            ss = new stone((Math.floor(s.x/blocksize)-2)*blocksize, (Math.floor(s.y/blocksize)-2)*blocksize);//tl2
            if(!pos.contains(ss) && !oppPos.contains(ss)) {
                stone dot1 = new stone(ss.x, ss.y, true);
                moveDot.add(dot1);
            }
        }
        ss = new stone((Math.floor(s.x/blocksize)+1)*blocksize, (Math.floor(s.y/blocksize)-1)*blocksize);//tr
        if(!pos.contains(ss) && !oppPos.contains(ss)) {
            stone dot1 = new stone(ss.x, ss.y, true);
            moveDot.add(dot1);
        } else if (!pos.contains(ss) && oppPos.contains(ss)) {
            possibleKills.add(ss);
            ss = new stone((Math.floor(s.x/blocksize)+2)*blocksize, (Math.floor(s.y/blocksize)-2)*blocksize);//tr2
            if(!pos.contains(ss) && !oppPos.contains(ss)) {
                stone dot1 = new stone(ss.x, ss.y, true);
                moveDot.add(dot1);
            }
        }
        // =========================== IF DAMONE ==========================================================================
        if (s.damone){
            ss = new stone((Math.floor(s.x/blocksize)-1)*blocksize, (Math.floor(s.y/blocksize)+1)*blocksize);//bl
            if(!pos.contains(ss) && !oppPos.contains(ss)) {
                stone dot1 = new stone(ss.x, ss.y, true);
                moveDot.add(dot1);
            } else if (!pos.contains(ss) && oppPos.contains(ss)) {
                possibleKills.add(ss);
                ss = new stone((Math.floor(s.x/blocksize)-2)*blocksize, (Math.floor(s.y/blocksize)+2)*blocksize);//bl2
                if(!pos.contains(ss) && !oppPos.contains(ss)) {
                    stone dot1 = new stone(ss.x, ss.y, true);
                    moveDot.add(dot1);
                }
            }
            ss = new stone((Math.floor(s.x/blocksize)+1)*blocksize, (Math.floor(s.y/blocksize)+1)*blocksize);//br
            if(!pos.contains(ss) && !oppPos.contains(ss)) {
                stone dot1 = new stone(ss.x, ss.y, true);
                moveDot.add(dot1);
            } else if (!pos.contains(ss) && oppPos.contains(ss)) {
                possibleKills.add(ss);
                ss = new stone((Math.floor(s.x/blocksize)+2)*blocksize, (Math.floor(s.y/blocksize)+2)*blocksize);//br2
                if(!pos.contains(ss) && !oppPos.contains(ss)) {
                    stone dot1 = new stone(ss.x, ss.y, true);
                    moveDot.add(dot1);
                }
            }
        }
        repaint();
    }

    private String createGameState(){
        String gs = "";
        for (stone s : oppPos){
            gs = gs+String.valueOf(s.x/blocksize)+String.valueOf(s.y/blocksize)+String.valueOf(checkDamone(oppPos, s)).substring(0,1)+",";
        }
        if (!oppPos.isEmpty()) gs = gs.substring(0, gs.length()-1);
        gs += ":";
        for (stone s : pos){
            gs = gs+String.valueOf(s.x/blocksize)+String.valueOf(s.y/blocksize)+String.valueOf(checkDamone(pos, s)).substring(0,1)+",";
        }
        if (!pos.isEmpty()) gs = gs.substring(0, gs.length()-1);
        return gs;
    }

    private boolean checkDamone(ArrayList<stone> pos, stone target){

        boolean r = false;
        for (stone s : pos){
            if (target.equals(s)) r = s.damone;
        }
        return r;
    }

    private boolean checkBecomeDamone(stone ss, stone s){

        if (ss.y/blocksize==0) return true;
        else return s.damone;
    }

    //===================================================================================================== OTHER GUY ============================================================================

    public void handleNextMove(String gs) {
 
        gameState = gs;
        String[] gss = gs.split(":");
        String pgs = gss[0]; //player
        String[] pgss = pgs.split(","); //si presenta tipo {"0.01.0", "1.02.0" ...}
        pos.clear();
        for (String p : pgss){
            if (p.length()>0){
                double y = Double.valueOf(p.substring(3, 6));
                if (y==7) y=0;
                else if (y==6) y=1;
                else if (y==5) y=2;
                else if (y==4) y=3;
                else if (y==3) y=4;
                else if (y==2) y=5;
                else if (y==1) y=6;
                else if (y==0) y=7;
                double x = Double.valueOf(p.substring(0, 3));
                if (x==7) x=0;
                else if (x==6) x=1;
                else if (x==5) x=2;
                else if (x==4) x=3;
                else if (x==3) x=4;
                else if (x==2) x=5;
                else if (x==1) x=6;
                else if (x==0) x=7;
                String d = p.substring(6, 7);
                stone s = new stone(d, x*blocksize, y*blocksize);
                pos.add(s);
            }
        }
        String ogs = gss[1]; //opponent
        String[] ogss = ogs.split(","); //si presenta tipo {"0.01.0", "1.02.0" ...}
        oppPos.clear();
        for (String p : ogss){
            if (p.length()>0){
                double y = Double.valueOf(p.substring(3, 6));
                if (y==7) y=0;
                else if (y==6) y=1;
                else if (y==5) y=2;
                else if (y==4) y=3;
                else if (y==3) y=4;
                else if (y==2) y=5;
                else if (y==1) y=6;
                else if (y==0) y=7;
                double x = Double.valueOf(p.substring(0, 3));
                if (x==7) x=0;
                else if (x==6) x=1;
                else if (x==5) x=2;
                else if (x==4) x=3;
                else if (x==3) x=4;
                else if (x==2) x=5;
                else if (x==1) x=6;
                else if (x==0) x=7;
                String d = p.substring(6, 7);
                stone s = new stone(d, x*blocksize, y*blocksize);
                oppPos.add(s);
            }
        }
        yourTurn = true;
        repaint();
        checkWin();
    }

    //=========================================================================================================== CHECK WIN  =====================================================================

    private boolean checkWin(){
        
        checkers game = this;
        if (pos.isEmpty() || oppPos.isEmpty()) {
            new java.util.Timer().schedule( 
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            gameroom.returnToLobby(game);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } 
                },  1000
            );
            return true;
        }
        else return false;
    }

    //=========================================================================================================== PAINTING  ======================================================================

    public void resetGrid(){

        for (int i=0;i<8;i++){
            stone s = new stone();
            if(!(i%2==0)){
                s.x = i*blocksize;
                s.y = 6*blocksize;
            } else {
                stone s1 = new stone();
                s.x = i*blocksize;
                s.y = 5*blocksize;
                s1.x = i*blocksize;
                s1.y= 7*blocksize;
                pos.add(s1);
            }
            pos.add(s);
        }
        for (int i=0;i<8;i++){
            stone s = new stone();
            if(i%2==0 || i==0){
                s.x = i*blocksize;
                s.y = 1*blocksize;
            } else {
                stone s1 = new stone();
                s.x = i*blocksize;
                s.y = 0*blocksize;
                s1.x = i*blocksize;
                s1.y= 2*blocksize;
                oppPos.add(s1);
            }
            oppPos.add(s);
        }
        //gameState = createGameState();
        repaint();
    }

    public void paintComponent(Graphics g){

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);

        for (int i =0; i<8; i++){

            if(i%2==0 || i==0) {
                g2d.fillRect(i*blocksize, 1*blocksize, blocksize, blocksize);
                g2d.fillRect(i*blocksize, 3*blocksize, blocksize, blocksize);
                g2d.fillRect(i*blocksize, 5*blocksize, blocksize, blocksize);
                g2d.fillRect(i*blocksize, 7*blocksize, blocksize, blocksize);
            } else{
                g2d.fillRect(i*blocksize, 0*blocksize, blocksize, blocksize);
                g2d.fillRect(i*blocksize, 2*blocksize, blocksize, blocksize);
                g2d.fillRect(i*blocksize, 4*blocksize, blocksize, blocksize);
                g2d.fillRect(i*blocksize, 6*blocksize, blocksize, blocksize);
            }
        }
        for(stone s : pos){
            g2d.setColor(Color.blue);
            if (s.damone) g2d.setColor(Color.CYAN);
            g2d.fill(s);
        }
        for(stone s : oppPos){
            g2d.setColor(Color.red);
            if (s.damone) g2d.setColor(Color.magenta);
            g2d.fill(s);
        }
        for(stone d : moveDot){
            g2d.setColor(Color.orange);
            d.x += 12;
            d.y += 12;
            g2d.fill(d);
        }
    }
}
