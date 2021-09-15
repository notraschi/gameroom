package src2.tictactoe;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.geom.*;
import java.io.IOException;
import java.awt.Polygon;
import src2.App;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class tictactoe extends JPanel{

    int blocksize = 100;
    public boolean yourTurn = false;
    public boolean isCircle = false;
    boolean won = false;
    boolean lost = false;
    int[][] winConditions = {{1,2,3}, {4,5,6}, {7,8,9}, {1,4,7}, {2,5,8}, {3,6,9}, {1,5,9}, {3,5,7}};
    int moveNumber = 0;
    public int wins = 0;
    int losses = 0;
    boolean host;

    App gameroom;

    public ArrayList<int[]> positions = new ArrayList<int[]>();
    public ArrayList<int[]> opponetPositions = new ArrayList<int[]>();
    public ArrayList<int[]> positionsHelper = new ArrayList<int[]>();
    
    ArrayList<Ellipse2D.Double> signArrayList = new ArrayList<Ellipse2D.Double>();
    ArrayList<Polygon> opponentSignArrayList = new ArrayList<Polygon>();

    public JLabel tttInfo = new JLabel();
    JLabel tttWins = new JLabel("0 - 0", 0);

    public tictactoe(App parent, boolean h){
        setBounds(50, 50, 400, 300);
        setVisible(true);
        setLayout(null);
        gameroom = parent;
        tttInfo.setBounds(90, 310, 160, 50);
        tttWins.setBounds(100, 350, 100, 30);
        tttInfo.setVisible(true);
        tttInfo.setAlignmentX(0.5f);
        tttInfo.setHorizontalAlignment(0);
        add(tttInfo);
        add(tttWins);
        host = h;
        if (!host) tttInfo.setText("waiting for opponent...");
    }

    public void paintGrid(){
        
        signArrayList.clear();
        for (int[] i : positions){
            Ellipse2D.Double sign = new Ellipse2D.Double();
            sign.setFrame(i[0]*blocksize, i[1]*blocksize, 100, 100);
            signArrayList.add(sign);
        }
        opponentSignArrayList.clear();
        for (int[] i : opponetPositions){
            int[] xs = {i[0]*blocksize, (i[0]*blocksize)+blocksize/2, (i[0]*blocksize)+blocksize, (i[0]*blocksize)+blocksize/2, (i[0]*blocksize)+blocksize, (i[0]*blocksize)+blocksize/2, i[0]*blocksize, (i[0]*blocksize)+blocksize/2};
            int[] ys = {i[1]*blocksize, (i[1]*blocksize)+blocksize/2, i[1]*blocksize, (i[1]*blocksize)+blocksize/2, (i[1]*blocksize)+blocksize, (i[1]*blocksize)+blocksize/2, (i[1]*blocksize)+blocksize, (i[1]*blocksize)+blocksize/2};
            Polygon sign = new Polygon(xs, ys, 8);
            opponentSignArrayList.add(sign);
        }
        repaint();
    }

    //================================================================================================== MOVING

    public String nextMove(String move){

        moveNumber++;
        int[] a = handleMove(move);        
        positions.add(a);
        yourTurn = false;
        paintGrid();
        tttInfo.setText("waiting for opponent...");
        if (checkWin(positions)) {
            tttInfo.setText("WON"); 
            wins++;
            tttWins.setText(String.valueOf(wins)+" - "+String.valueOf(losses));
            checkReturnToLobby();
            yourTurn = false;
            restart(false);
        }
        if(moveNumber > 8) restart(false);
        return move;
    }

    public int[] handleMove(String move) {
        
        String x = move.substring(0,1);
        String y = move.substring(2, 3);
        int[] a = {Integer.valueOf(x), Integer.valueOf(y)};
        return a;
    }

    public void handleOpponetMove(String move){

        moveNumber++;
        int[] a = handleMove(move);
        opponetPositions.add(a);
        yourTurn = true; 
        tttInfo.setText("it's your turn..."); 
        if (checkWin(opponetPositions)) {
            tttInfo.setText("LOST"); 
            losses++;
            tttWins.setText(String.valueOf(wins)+" - "+String.valueOf(losses));
            checkReturnToLobby();
            yourTurn = false;
            if (wins!=1 || losses!=1) restart(true);
            else restart(false);
        }
        paintGrid();
        if(moveNumber > 8) restart(true);
    }

    public void restart(boolean y){

        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    moveNumber = 0;
                    positions.clear();
                    positionsHelper.clear();
                    opponetPositions.clear();
                    paintGrid();
                    yourTurn = y;
                    if (yourTurn) tttInfo.setText("it's your turn..."); 
                    else tttInfo.setText("waiting for opponent..."); 
                } 
            },  1000
        );
    }

    public boolean checkMove(int x, int y){
        int rx = x*100;
        int ry = y*100;
        for (Ellipse2D.Double s : signArrayList){
            if (s.x == rx && s.y == ry) return false;
        }
        for (Polygon s : opponentSignArrayList){
            if (s.xpoints[0] == rx && s.ypoints[0] == ry) return false;
        }

        return true;
    }

    private void checkReturnToLobby(){

        tictactoe game = this;
        if (wins == 3 || losses == 3){
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
        }
    }

    //================================================================================================== OTHER 

    public boolean checkWin(ArrayList<int[]> pp){
        
        ArrayList<Integer> pos = new ArrayList<Integer>();
        checkWinHelper(pp.get(0), pos);
        if(pp.size()>1) checkWinHelper(pp.get(1), pos);
        if(pp.size()>2) checkWinHelper(pp.get(2), pos);
        if(pp.size()>3) checkWinHelper(pp.get(3), pos);
        if(pp.size()>4) checkWinHelper(pp.get(4), pos);
        for (int[] i : winConditions){
            if (pos.contains(i[0]) && pos.contains(i[1]) && pos.contains(i[2])) return true;
        }
        return false;
    }
   
    private void checkWinHelper(int[] is, ArrayList<Integer> pos) {
        int i = 0;
        int nn;
        if (Arrays.toString(is).equals("[0, 0]")) i=1;
        else if (Arrays.toString(is).equals("[0, 1]")) i=2; 
        else if (Arrays.toString(is).equals("[0, 2]")) i=3; 
        else if (Arrays.toString(is).equals("[1, 0]")) i=4; 
        else if (Arrays.toString(is).equals("[1, 1]")) i=5; 
        else if (Arrays.toString(is).equals("[1, 2]")) i=6; 
        else if (Arrays.toString(is).equals("[2, 0]")) i=7; 
        else if (Arrays.toString(is).equals("[2, 1]")) i=8; 
        else if (Arrays.toString(is).equals("[2, 2]")) i=9;
        nn = i;
        pos.add(nn);
    }

    public void paintComponent (Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.BLACK);
        for (int i =1; i<3; i++){

            g2d.drawLine(0, i *blocksize, 300, i*blocksize);
            g2d.drawLine(i*blocksize, 0, i*blocksize, 300);
        }

        g2d.setColor(Color.BLUE);
        for (Ellipse2D.Double ed : signArrayList){
            g2d.draw(ed);
        }
        g2d.setColor(Color.RED);
        for (Polygon ed : opponentSignArrayList){
            g2d.draw(ed);
        }
    }
}
