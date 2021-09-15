package src2.fingergame;

import javax.swing.JButton;

public class hand extends JButton{
    
    boolean dead = false;
    boolean isEnemy;
    int value = 1;

    hand(boolean isenemy, int x, int y, int w, int h){
        isEnemy = isenemy;
        if (isEnemy) setEnabled(false);
        setBounds(x, y, w, h);
        setText(String.valueOf(value));
    }

    public int getHit(hand otherHand){

        if(isEnemy){
            value += otherHand.value;
            setText(String.valueOf(value));
            if (value>4 || value<1) {
                value = 0;
                dead = true;
                setText("D");
            }
        } else {
            dead = false;
            value = (otherHand.value)/2;
            setText(String.valueOf(value));
            otherHand.split();
        }

        return value;
    }

    private void split() {

        value /=2;
        dead =false;
        setText(String.valueOf(value));
    }

    public void checkDead() {

        if (value>4 || value<1) {
            value = 0;
            dead = true;
            setText("D");
            setEnabled(false);
        } else {
            dead = false;
        }
    }
}
