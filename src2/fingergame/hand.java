package src2.fingergame;

import java.awt.Color;

import javax.swing.BorderFactory;
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

    @Override
    public void setEnabled(boolean b) {
        if (!b && model.isRollover()) {
            model.setRollover(false);
        }
        super.setEnabled(b);
        model.setEnabled(b);
        if (b) this.setBorder(BorderFactory.createLineBorder(new Color(54, 38, 27), 2, false));
        else this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, false));
    }
}
