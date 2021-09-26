package src2;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.event.*;
import java.io.IOException;

public class App extends JFrame implements ActionListener, MouseListener{       // new Color(246, 228, 162) [BG]    // new Color(54, 38, 27) [CONTRAST]

    JPanel p = new JPanel();
    JButton cliButton = new JButton("JOIN");
    JButton srvButton = new JButton("HOST");
    JTextField cipField = new JTextField("192.168.8.130"); //"192.168.8.130"  " ip here"
    JTextField cportField = new JTextField("6969"); // "6969"    " port here"
    JTextField sportField = new JTextField("6969"); // "6969"    " port here"
    JButton tictactoeButton = new JButton("TRIS");
    JButton fingergameButton = new JButton("FINGER GAME");
    JButton checkersButton = new JButton("CHECKERS");
    JLabel VERSION = new JLabel("gameroom v1.0 - 09/21"); //fixed bug in checkers

    JPanel test = new JPanel();

    JButton[] UIButtons = {cliButton, srvButton};
    JTextField[] UITextField = {cportField, cipField,  sportField};

    server server = new server();
    client client = new client();

    boolean host;
    String choosenGame;

    tictactoe tttGame; //CREATE TICTACTOE
    boolean tttRunning = false;

    fingergame fgGame; //CREATE [IL GIOCO QUELLO CON LE DITA] AKA FINGERGAME :)

    checkers chGame; //CREATE CHECKERS [DAMA]
    boolean chRunning = false;
    
    public static void main(String[] args) throws Exception {
        new App();
    }

    App(){

        VERSION.setBounds(03,390,200, 25);
        p.setBackground(new Color(246, 228, 162));
        p.add(VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setVisible(true);
        setSize(320, 450);
        setResizable(false);
        p.setSize(320, 350);
        p.addMouseListener(this);
        add(p);
        p.setLayout(null);
        cliButton.setBounds(50, 75, 75, 50);
        cliButton.addActionListener(this);
        p.add(cliButton);
        srvButton.setBounds(175, 75, 75, 50);
        srvButton.addActionListener(this);
        p.add(srvButton);
        tictactoeButton.addActionListener(this);
        fingergameButton.addActionListener(this);
        checkersButton.addActionListener(this);

        cipField.setBounds(50, 150, 75, 30);
        cportField.setBounds(50, 200, 75, 30);
        sportField.setBounds(175, 150, 75, 30);
        p.add(cportField);
        p.add(cipField);
        p.add(sportField);
        cipField.getText();

        for (JTextField i : UITextField){
            i.setBackground(Color.WHITE);
            i.setBorder(BorderFactory.createLineBorder(new Color(54, 38, 27), 2, false));
        }
            
        for (JButton i : UIButtons){
            i.setBackground(Color.WHITE);
            i.setBorder(BorderFactory.createLineBorder(new Color(54, 38, 27), 2, false));
        }
    }

    //================================================================================================= HANDLING COMMUNICATION ======================================================================

    public void lobby(boolean h) throws IOException {

        VERSION.setForeground(new Color(246, 228, 162));

        for (JTextField i : UITextField){
        p.remove(i);}
        
        for (JButton i : UIButtons){
        p.remove(i);}

        p.setBackground(new Color(54, 38, 27));
        
        if (host) {
            tictactoeButton.setBounds(75, 50, 150, 50);
            tictactoeButton.setBackground(Color.WHITE);
            tictactoeButton.setBorder(BorderFactory.createLineBorder(new Color(246, 228, 162), 2, false));
            tictactoeButton.setFocusable(false);
            p.add(tictactoeButton);
            
            fingergameButton.setBounds(75, 150, 150, 50);
            fingergameButton.setBackground(Color.WHITE);
            fingergameButton.setBorder(BorderFactory.createLineBorder(new Color(246, 228, 162), 2, false));
            fingergameButton.setFocusable(false);
            p.add(fingergameButton);

            checkersButton.setBounds(75, 250, 150, 50);
            checkersButton.setBackground(Color.WHITE);
            checkersButton.setBorder(BorderFactory.createLineBorder(new Color(246, 228, 162), 2, false));
            checkersButton.setFocusable(false);
            p.add(checkersButton);
        }
        
        p.revalidate();

        if (h==false) {
            choosenGame = client.reciveMessage();
            connectToChosenGame();
        }
    }

    public void returnToLobby (JComponent game) throws IOException {

        chRunning = false;
        tttRunning = false;

        remove(game);
        revalidate();
        p.setSize(320, 350);
        p.addMouseListener(this);
        add(p);
        p.setBackground(new Color(54, 38, 27));
        revalidate();
        repaint();

        if (host) {
            p.add(tictactoeButton);
            p.add(fingergameButton);
            p.add(checkersButton);
        }
        
        p.revalidate();

        if (host==false) {
            choosenGame = client.reciveMessage();
            connectToChosenGame();
        }
    }
    
    private void connectToChosenGame() throws IOException {

        switch (choosenGame){

            case "ttt": tictactoeStart();
                tttRunning = true;
                break;
            case "fg": fingerGameStart();
                break;
            case "ch": checkersStart();
                break;
            default: break;
        }
        
    }

    //=============================================================================== HANDLING TIC TAC TOE ============================================================ 

    public void tictactoeStart() throws IOException {

        tttGame = new tictactoe(this, host);
        remove(p);
        add(tttGame);   //ADDING TICTACTOE
        tttGame.addMouseListener(this);
        repaint();
        revalidate();
        if (host){server.sendMessage("ttt\n");
            System.out.println("pussy ass bitch");
            tttGame.yourTurn = true;
            tttGame.isCircle = true;
            tttGame.tttInfo.setText("it's your turn...");
        } else if (!host) tttGame.handleOpponetMove(client.reciveMessage());
    }

    public void tictactoeNextTurn(String move) throws IOException{

        if (host) {server.sendMessage(move+"\n");
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    try {
                        tttGame.handleOpponetMove(server.reciveMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } 
                }
            },  1000
        );}
        else if (!host ) {client.sendMessage(move+"\n");
            new java.util.Timer().schedule( 
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            tttGame.handleOpponetMove(client.reciveMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } 
                    }
                },  1000
            );
        }
    }

    public void tictactoeLastTurn(String move) throws IOException {
        
        if (host) server.sendMessage(move+"\n");
        else if (!host) client.sendMessage(move+"\n");
    }

    //=============================================================================== HANDLING FINGER GAME ==========================================================================

    private void fingerGameStart() throws IOException{

        remove(p);
        fgGame = null;
        if(host){server.sendMessage("fg\n");
            fgGame = new fingergame(true, this, "it's your turn...");
            fgGame.firstClick = true;
        } else if (!host) {
            fgGame = new fingergame(false, this, "waiting for opponent...");
            fgGame.handleGameState(client.reciveMessage());
        }
        add(fgGame);
        revalidate();
    }

    public void fingergameNextTurn(String gameState) throws IOException{

        if (host) {server.sendMessage(gameState);
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    try {
                        fgGame.handleGameState(server.reciveMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } 
                }
            },  1000
        );}
        else if (!host) {client.sendMessage(gameState);
            new java.util.Timer().schedule( 
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            fgGame.handleGameState(client.reciveMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } 
                    }
                },  1000
            );
        }
    }

    public void fingergameLastTurn(String gameState) throws IOException {
        
        if (host) server.sendMessage(gameState);
        else if (!host) client.sendMessage(gameState);
    }

    //================================================================================= HANDLING CHECKERS ================================================================================

    public void checkersStart() throws IOException{

        remove(p);
        revalidate();
        repaint();
        if(host){server.sendMessage("ch\n");
            chGame = new checkers(true, this);
            chGame.yourTurn = true;
        } else if (!host) {
            chGame = new checkers(false, this);
            chGame.yourTurn = false;
            chGame.handleNextMove(client.reciveMessage());
        }
        chRunning = true;
        add(chGame);
        chGame.addMouseListener(this);
        revalidate();
        repaint();
    }

    public void checkersNextTurn(String gameState) throws IOException{

        if (host) {server.sendMessage(gameState);
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    try {
                        chGame.handleNextMove(server.reciveMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } 
                }
            },  1000
        );}
        else if (!host) {client.sendMessage(gameState);
            new java.util.Timer().schedule( 
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        try {
                            chGame.handleNextMove(client.reciveMessage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } 
                    }
                },  1000
            );
        }
    }

    public void checkersLastTurn(String gameState) throws IOException {
        
        if (host) server.sendMessage(gameState);
        else if (!host) client.sendMessage(gameState);
    }

    //================================================================================= HANDLING ACTIONS =================================================================================
   
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource().equals(srvButton) && !sportField.getText().equals("")){    //SERVER
            try {
                host = true;
                server.portn = Integer.valueOf(sportField.getText());
                server.connect();
                lobby(true);    //going to where you choose the game
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource().equals(cliButton) && !cipField.getText().equals("") && !cportField.getText().equals("")){    //CLIENT 
            try {
                host =false;
                client.portn = Integer.valueOf(cportField.getText());
                client.ip = cipField.getText();
                client.connect();
                lobby(false);    //going to where you choose the game
            } catch (IOException e1) {
                System.out.println("no host found");
            }
        } else if (e.getSource().equals(tictactoeButton)){ //HOST CHOSE TICTACTOE
            try {
                tictactoeStart();
                tttRunning = true;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource().equals(fingergameButton)){
            try {
                fingerGameStart();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource().equals(checkersButton)){
            try {
                checkersStart();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    //======================================================================================= MOUSE

    @Override
    public void mouseClicked(MouseEvent e) {
        if (tttRunning && tttGame.yourTurn && e.getY()<=300){    
            int x = (int) Math.floor(e.getX()/100);
            int y = (int) Math.floor(e.getY()/100);
            String s = String.valueOf(x);
            String ss = String.valueOf(y);
            int[] a = {x, y};
            tttGame.positionsHelper.add(a);
            if (tttGame.checkMove(x, y)) {
                tttGame.nextMove(s+":"+ss);
            }
        } else if (chRunning && chGame.yourTurn && e.getY()<=300){         
            chGame.validateMove((double) (e.getX()-4)/(checkers.blocksize), (double) (e.getY()-4)/(checkers.blocksize));
        } 
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
}
