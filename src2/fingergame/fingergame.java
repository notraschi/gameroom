package src2.fingergame;

import javax.swing.JLabel;
import javax.swing.JPanel;
import src2.App;
import java.awt.event.*;
import java.io.IOException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class fingergame extends JPanel implements ActionListener{

    App gameroom;
    JLabel fgInfo;

    public boolean firstClick = false;
    boolean secondClick = false;
    hand attackHand;
    hand targetHand;

    public int wins = 0;
    int losses = 0;

    String gameState = "1111";

    hand h1 = new hand(false, 50, 200, 50, 50); //your hands        //..EH1.....EH2..//
    hand h2 = new hand(false, 210, 200, 50, 50);                    //...............//
    hand eh1 = new hand(true, 50, 50, 50, 50); //enemy hands        //...............//
    hand eh2 = new hand(true, 210, 50, 50, 50);                     //...H1.....H2...//
    hand[] hands = {h1, h2, eh1, eh2};

    JLabel fgWins = new JLabel("0 - 0", 0);

    public fingergame(boolean enabled, App parent, String fgInfoText){

        setLayout(null);
        setBounds(0,0,400,500);
        setVisible(true);
        setFocusable(true);

        fgInfo = new JLabel(fgInfoText, 0);
        fgInfo.setBounds(80, 135, 140, 30);
        fgWins.setBounds(100, 350, 100, 30);
        h1.addActionListener(this);
        h2.addActionListener(this);
        eh1.addActionListener(this);
        eh2.addActionListener(this);
        h1.setEnabled(enabled);
        h2.setEnabled(enabled);
        eh1.setEnabled(false);
        eh2.setEnabled(false);
        add(h1);
        add(h2);
        add(eh1);
        add(eh2);
        add(fgInfo);
        add(fgWins);

        gameroom = parent;
        repaint();
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
    }

    public void sendGameState() throws IOException{

        String sh1 = String.valueOf(h1.value);
        String sh2 = String.valueOf(h2.value);
        String seh1 = String.valueOf(eh1.value);
        String seh2 = String.valueOf(eh2.value);
        gameState = sh1+sh2+seh1+seh2;
        if (checkWin()==2) {
            fgInfo.setText("WON");
            wins++;
            fgWins.setText(String.valueOf(wins+" - "+losses));
            if (wins>2) {
                System.out.println("last turn");
                gameroom.fingergameLastTurn(gameState+"\n");
                checkReturnToLobby();
            } else {
                restart(false);
                gameroom.fingergameNextTurn(gameState+"\n");
            }
        }
        else {
            fgInfo.setText("waiting for opponent...");  
            gameroom.fingergameNextTurn(gameState+"\n");
        }
    }

    public void handleGameState(String gameState){

        System.out.println("now handling");
        h1.value = Integer.valueOf(gameState.substring(2, 3));
        h2.value = Integer.valueOf(gameState.substring(3, 4));
        eh1.value = Integer.valueOf(gameState.substring(0, 1));
        eh2.value = Integer.valueOf(gameState.substring(1, 2));
        h1.setEnabled(true);
        h2.setEnabled(true);
        revalidate();
        repaint();
        for (hand h : hands) {
            h.setText(String.valueOf(h.value));
            h.checkDead();
        }
        firstClick = true;
        if (checkWin()==1) {
            fgInfo.setText("LOST");
            losses++;
            fgWins.setText(String.valueOf(wins+" - "+losses));
            if (losses>2) checkReturnToLobby();
            else restart(true);}
        else fgInfo.setText("it's your turn...");
    }

    private int checkWin() {

        if(h1.dead && h2.dead) return 1;
        else if (eh1.dead && eh2.dead) return 2;
        else return 3;
    }

    private boolean checkReturnToLobby(){

        fingergame game = this;
        if (wins > 2 || losses > 2){
            wins = 0;
            losses = 0;
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

    private void restart(boolean enabled) {

        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    gameState = "1111";
                    h1.setEnabled(enabled);
                    h2.setEnabled(enabled);
                    eh1.setEnabled(false);
                    eh2.setEnabled(false);
                    for (hand h : hands) {
                        h.value = 1;
                        h.setText(String.valueOf(h.value));
                    }
                    if (enabled) fgInfo.setText("it's your turn...");
                    else fgInfo.setText("waiting for opponent...");
                } 
            },  1000
        );
    }

    //===================================================================================================== MOVES ==========================

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource().equals(h1)){ // ================== H1

            if(firstClick){
                firstClick = false;
                attackHand = h1;
                if (h2.dead && (h1.value==2 || h1.value==4)) h2.setEnabled(true);
                else h2.setEnabled(false);
                h1.setEnabled(false);
                if (!eh1.dead) eh1.setEnabled(true);
                if (!eh2.dead) eh2.setEnabled(true);
                secondClick = true;
            } else if (h1.dead && secondClick && (h2.value==2 || h2.value==4)) {
                secondClick = false;
                h1.dead = false;
                h1.setEnabled(false);
                eh2.setEnabled(false);
                eh1.setEnabled(false);
                h1.getHit(attackHand);
                try {
                    sendGameState();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (e.getSource().equals(h2)) { // ================== H2

            if(firstClick){
                firstClick = false;
                attackHand = h2;
                h2.setEnabled(false);
                if (h1.dead && (h2.value==2 || h2.value==4)) h1.setEnabled(true);
                else h1.setEnabled(false);
                if (!eh1.dead) eh1.setEnabled(true);
                if (!eh2.dead) eh2.setEnabled(true);
                secondClick = true;
            } else if (h2.dead && secondClick && (h1.value==2 || h1.value==4)) {
                secondClick = false;
                h2.dead = false;
                h2.setEnabled(false);
                eh2.setEnabled(false);
                eh1.setEnabled(false);
                h2.getHit(attackHand);
                try {
                    sendGameState();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (e.getSource().equals(eh1)) { //=============== EH1

            if(secondClick){
                secondClick = false;
                eh2.setEnabled(false);
                eh1.setEnabled(false);
                h1.setEnabled(false);
                h2.setEnabled(false);
                eh1.getHit(attackHand);
                try {
                    sendGameState();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } else if (e.getSource().equals(eh2)) { //=========== EH2

            if(secondClick){
                secondClick = false;
                eh2.setEnabled(false);
                eh1.setEnabled(false);
                h1.setEnabled(false);
                h2.setEnabled(false);
                eh2.getHit(attackHand);
                try {
                    sendGameState();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    } 
}
