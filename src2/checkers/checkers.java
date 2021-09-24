package src2.checkers;

import javax.swing.JPanel;
import src2.App;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.BasicStroke;
import java.awt.Polygon;

public class checkers extends JPanel{
    
    public static int blocksize = 37;
    public int offset = 4;
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
    ArrayList<stone> comboDot = new ArrayList<stone>();
    ArrayList<stone> comboKillsTL = new ArrayList<stone>();
    ArrayList<stone> comboKillsTR = new ArrayList<stone>();
    ArrayList<stone> comboKillsBL = new ArrayList<stone>();
    ArrayList<stone> comboKillsBR = new ArrayList<stone>();

    public checkers(boolean turn, App p){
        setLayout(null);
        setOpaque(true);
        setSize(500,500);
        setVisible(true);
        yourTurn = turn;
        gameroom = p;
        resetGrid();
    }

    //===================================================================================================== ME MOVING ======================================================

    public void nextMove(stone dot, stone s, boolean combo){

        pos.remove(s);
        stone ss = new stone(dot.x-12, dot.y-12);
        ss.damone = checkBecomeDamone(ss, s);
        pos.add(ss);
        yourTurn = false;
        if(!combo){
            for (stone pk : possibleKills){ //killing
                if (pk.x == dot.x+37-12 || pk.x == dot.x-37-12) oppPos.remove(pk);
            }
        } else {
            System.out.println(comboDot.size()+" "+comboKillsBL.size());
            for (stone cd : comboDot){          // COMBO KILLS 
                if (cd.dotDirection.equals("tl")){
                    for (stone t : comboKillsTL){
                        if (comboKillsTL.indexOf(t)<getIndexComboDot(dot)+2) oppPos.remove(t);
                    }
                } else if (cd.dotDirection.equals("tr")){
                    for (stone t : comboKillsTR){
                        if (comboKillsTR.indexOf(t)<getIndexComboDot(dot)+2) oppPos.remove(t);
                    }
                } else if (cd.dotDirection.equals("bl")){
                    for (stone t : comboKillsBL){
                        if (comboKillsBL.indexOf(t)<getIndexComboDot(dot)+2) oppPos.remove(t);
                    }
                } else if (cd.dotDirection.equals("br")){
                    for (stone t : comboKillsBR){
                        if (comboKillsBR.indexOf(t)<getIndexComboDot(dot)+2) oppPos.remove(t);
                    }
                }
            }
        }
        comboKillsBL.clear();
        comboKillsBR.clear();
        comboKillsTL.clear();
        comboKillsTR.clear();
        comboDot.clear();
        moveDot.clear();
        repaint();
        gameState = createGameState();
        try {
            if (checkWin()) {gameroom.checkersLastTurn(gameState+"\n");}
            else gameroom.checkersNextTurn(gameState+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        checkWin();
    }

    private int getIndexComboDot(stone dot){

        for(stone s : comboDot){
            if(s.x==dot.x && s.y==dot.y) {/*System.out.println(comboDot.indexOf(s)); */return comboDot.indexOf(s);}
        }
        return 0;
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
            nextMove(dot, movingStone, false);
            return true;
        } else if (comboDot.contains(dot)) {
            nextMove(dot, movingStone, true);
            return true;
        }
        else return false;
    }

    public void calculatePossibleMoves(stone s){

        moveDot.clear();
        comboDot.clear();
        possibleKills.clear();
        stone ss = new stone((Math.floor(s.x/blocksize)-1)*blocksize, (Math.floor(s.y/blocksize)-1)*blocksize);//tl
        if(!pos.contains(ss) && !oppPos.contains(ss)) {
            stone dot1 = new stone(ss.x, ss.y, true);
            moveDot.add(dot1);
        } else if (!pos.contains(ss) && oppPos.contains(ss)) {
            possibleKills.add(ss);
            stone sss = ss;
            ss = new stone((Math.floor(s.x/blocksize)-2)*blocksize, (Math.floor(s.y/blocksize)-2)*blocksize);//tl2
            if(!pos.contains(ss) && !oppPos.contains(ss)) {
                stone dot1 = new stone(ss.x, ss.y, true);
                moveDot.add(dot1);
                checkCombo(ss, s.damone, "null", sss, "br");
            }
        }
        ss = new stone((Math.floor(s.x/blocksize)+1)*blocksize, (Math.floor(s.y/blocksize)-1)*blocksize);//tr
        if(!pos.contains(ss) && !oppPos.contains(ss)) {
            stone dot1 = new stone(ss.x, ss.y, true);
            moveDot.add(dot1);
        } else if (!pos.contains(ss) && oppPos.contains(ss)) {
            possibleKills.add(ss);
            stone sss = ss;
            ss = new stone((Math.floor(s.x/blocksize)+2)*blocksize, (Math.floor(s.y/blocksize)-2)*blocksize);//tr2
            if(!pos.contains(ss) && !oppPos.contains(ss)) {
                stone dot1 = new stone(ss.x, ss.y, true);
                moveDot.add(dot1);
                checkCombo(ss, s.damone, "null", sss, "bl");
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
                stone sss = ss;
                ss = new stone((Math.floor(s.x/blocksize)-2)*blocksize, (Math.floor(s.y/blocksize)+2)*blocksize);//bl2
                if(!pos.contains(ss) && !oppPos.contains(ss)) {
                    stone dot1 = new stone(ss.x, ss.y, true);
                    moveDot.add(dot1);
                    checkCombo(ss, true, "null", sss, "tr");
                }
            }
            ss = new stone((Math.floor(s.x/blocksize)+1)*blocksize, (Math.floor(s.y/blocksize)+1)*blocksize);//br
            if(!pos.contains(ss) && !oppPos.contains(ss)) {
                stone dot1 = new stone(ss.x, ss.y, true);
                moveDot.add(dot1);
            } else if (!pos.contains(ss) && oppPos.contains(ss)) {
                possibleKills.add(ss);
                stone sss = ss;
                ss = new stone((Math.floor(s.x/blocksize)+2)*blocksize, (Math.floor(s.y/blocksize)+2)*blocksize);//br2
                if(!pos.contains(ss) && !oppPos.contains(ss)) {
                    stone dot1 = new stone(ss.x, ss.y, true);
                    moveDot.add(dot1);
                    checkCombo(ss, true, "null", sss, "tl");
                }
            }
        }
        repaint();
    }

    private void checkCombo (stone s, boolean damone, String cd, stone sss, String exC) {

        stone ts = new stone((Math.floor(s.x/blocksize)-1)*blocksize, (Math.floor(s.y/blocksize)-1)*blocksize); //the one we eat         //tl2
        stone ss = new stone((Math.floor(s.x/blocksize)-2)*blocksize, (Math.floor(s.y/blocksize)-2)*blocksize); //where we end up after combo
        if(!pos.contains(ts) && oppPos.contains(ts) && !pos.contains(ss) && !oppPos.contains(ss) && !exC.equals("tl") && ss.x>=0 && ss.x<=300 && ss.y<=300 && ss.y>=0) {
            if (cd.equals("tl")){
                comboKillsTL.add(ts);
            } else if (cd.equals("tr")){
                comboKillsTR.add(ts);
            } else if (cd.equals("bl")){
                comboKillsBL.add(ts);
            } else if (cd.equals("br")){
                comboKillsBR.add(ts);
            } else {comboKillsTL.add(ts); comboKillsTL.add(sss); cd="tl";}
            stone dot1 = new stone(ss.x, ss.y, true, cd);
            comboDot.add(dot1);
            checkCombo(ss, damone, cd, null, "br");
        }
        ts = new stone((Math.floor(s.x/blocksize)+1)*blocksize, (Math.floor(s.y/blocksize)-1)*blocksize); //the one we eat               //tr2
        ss = new stone((Math.floor(s.x/blocksize)+2)*blocksize, (Math.floor(s.y/blocksize)-2)*blocksize); //where we end up after combo
        if(!pos.contains(ts) && oppPos.contains(ts) && !pos.contains(ss) && !oppPos.contains(ss) && !exC.equals("tr") && ss.x>=0 && ss.x<=300 && ss.y<=300 && ss.y>=0) {
            if (cd.equals("tl")){
                comboKillsTL.add(ts);
            } else if (cd.equals("tr")){
                comboKillsTR.add(ts);
            } else if (cd.equals("bl")){
                comboKillsBL.add(ts);
            } else if (cd.equals("br")){
                comboKillsBR.add(ts);
            } else {comboKillsTR.add(ts); comboKillsTR.add(sss); cd="tr";}
            stone dot1 = new stone(ss.x, ss.y, true, cd);
            comboDot.add(dot1);
            checkCombo(ss, damone, cd, null, "bl");
        }
        if (damone) {                                                                                                                    //bl2
            ts = new stone((Math.floor(s.x/blocksize)-1)*blocksize, (Math.floor(s.y/blocksize)+1)*blocksize); //the one we eat
            ss = new stone((Math.floor(s.x/blocksize)-2)*blocksize, (Math.floor(s.y/blocksize)+2)*blocksize); //where we end up after combo
            if(!pos.contains(ts) && oppPos.contains(ts) && !pos.contains(ss) && !oppPos.contains(ss) && !exC.equals("bl") && ss.x>=0 && ss.x<=300 && ss.y<=300 && ss.y>=0) {
                if (cd.equals("tl")){
                    comboKillsTL.add(ts);
                } else if (cd.equals("tr")){
                    comboKillsTR.add(ts);
                } else if (cd.equals("bl")){
                    comboKillsBL.add(ts);
                } else if (cd.equals("br")){
                    comboKillsBR.add(ts);
                } else {comboKillsBL.add(ts); comboKillsBL.add(sss); cd="bl";}
                stone dot1 = new stone(ss.x, ss.y, true, cd);
                comboDot.add(dot1);
                checkCombo(ss, damone, cd, null, "tr");
            }
            ts = new stone((Math.floor(s.x/blocksize)+1)*blocksize, (Math.floor(s.y/blocksize)+1)*blocksize); //the one we eat             //br2
            ss = new stone((Math.floor(s.x/blocksize)+2)*blocksize, (Math.floor(s.y/blocksize)+2)*blocksize); //where we end up after combo
            if(!pos.contains(ts) && oppPos.contains(ts) && !pos.contains(ss) && !oppPos.contains(ss) && !exC.equals("br") && ss.x>=0 && ss.x<=300 && ss.y<=300 && ss.y>=0) {
                if (cd.equals("tl")){
                    comboKillsTL.add(ts);
                } else if (cd.equals("tr")){
                    comboKillsTR.add(ts);
                } else if (cd.equals("bl")){
                    comboKillsBL.add(ts);
                } else if (cd.equals("br")){
                    comboKillsBR.add(ts);
                } else {comboKillsBR.add(ts); comboKillsBR.add(sss); cd="br";}
                stone dot1 = new stone(ss.x, ss.y, true, cd);
                comboDot.add(dot1);
                checkCombo(ss, damone, cd, null, "tl");
            }
        }
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
        revalidate();
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
                },  3000
            );
            return true;
        }
        else return false;
    }

    //=========================================================================================================== PAINTING  ======================================================================

    public void resetGrid(){

        for (int i=0;i<8;i++){          //8
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
        for (int i=0;i<8;i++){          //8
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

        g2d.setColor(new Color(246, 228, 162));
        g2d.fillRect(0, 0, 400, 500);

        g2d.setColor(new Color(54, 38, 27));
        for (int i =0; i<8; i++){

            if(i%2==0 || i==0) {
                g2d.fillRect(i*blocksize+offset, 1*blocksize+offset, blocksize, blocksize);
                g2d.fillRect(i*blocksize+offset, 3*blocksize+offset, blocksize, blocksize);
                g2d.fillRect(i*blocksize+offset, 5*blocksize+offset, blocksize, blocksize);
                g2d.fillRect(i*blocksize+offset, 7*blocksize+offset, blocksize, blocksize);
            } else{
                g2d.fillRect(i*blocksize+offset, 0*blocksize+offset, blocksize, blocksize);
                g2d.fillRect(i*blocksize+offset, 2*blocksize+offset, blocksize, blocksize);
                g2d.fillRect(i*blocksize+offset, 4*blocksize+offset, blocksize, blocksize);
                g2d.fillRect(i*blocksize+offset, 6*blocksize+offset, blocksize, blocksize);
            }
        }
        g2d.setStroke(new BasicStroke(8));
        g2d.drawLine(2, 2, 2, 302);
        g2d.drawLine(2, 2, 302, 2);
        g2d.drawLine(302, 2, 302, 302);
        g2d.drawLine(2, 302, 302, 302);
        for(stone s : pos){     
            g2d.setColor(Color.blue);
            g2d.fillOval((int)s.x+offset, (int)s.y+offset, (int)s.width, (int)s.height);
            if (s.damone){
                g2d.setColor(Color.ORANGE); 
                int[] xp = {(int) (12+s.x)+offset, (int) (15+s.x)+offset, (int) (18+s.x)+offset, (int) (21+s.x)+offset, (int) (24+s.x)+offset, (int) (24+s.x)+offset, (int) (12+s.x)+offset};
                int[] yp = {(int) (12+s.y)+offset, (int) (18+s.y)+offset, (int) (12+s.y)+offset, (int) (18+s.y)+offset, (int) (12+s.y)+offset, (int) (24+s.y)+offset, (int) (24+s.y)+offset};
                Polygon p = new Polygon(xp, yp, 7);
                g2d.fillPolygon(p);
            }
        }
        for(stone s : oppPos){
            g2d.setColor(Color.red); 
            g2d.fillOval((int)s.x+offset, (int)s.y+offset, (int)s.width, (int)s.height);
            if (s.damone){
                g2d.setColor(Color.ORANGE); 
                int[] xp = {(int) (12+s.x)+offset, (int) (15+s.x)+offset, (int) (18+s.x)+offset, (int) (21+s.x)+offset, (int) (24+s.x)+offset, (int) (24+s.x)+offset, (int) (12+s.x)+offset};
                int[] yp = {(int) (12+s.y)+offset, (int) (18+s.y)+offset, (int) (12+s.y)+offset, (int) (18+s.y)+offset, (int) (12+s.y)+offset, (int) (24+s.y)+offset, (int) (24+s.y)+offset};
                Polygon p = new Polygon(xp, yp, 7);
                g2d.fillPolygon(p);
            }
        }
        for(stone d : moveDot){
            g2d.setColor(Color.orange);
            d.x += 12;
            d.y += 12;
            g2d.fillOval((int)d.x+offset, (int)d.y+offset, (int)d.width, (int)d.height);

        }
        for(stone d : comboDot){
            g2d.setColor(Color.orange);
            d.x += 12;
            d.y += 12;
            g2d.fillOval((int)d.x+offset, (int)d.y+offset, (int)d.width, (int)d.height);

        }
    }
}
