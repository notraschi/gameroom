package src2;
import java.net.*;
import java.io.*;

public class server {

    ServerSocket ss;
    Socket s;
    public int portn;

    //DataInputStream dis;
    BufferedReader br;
    DataOutputStream dos;

    public Socket connect() throws IOException{

        //making the port
        ss = new ServerSocket(portn);

        //enabling entrance
        s = ss.accept();
        s.setKeepAlive(true);

        System.out.println("server online");

        //no one else can entrace - just one guy can entrer
        ss.close();
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        //dis = new DataInputStream(s.getInputStream()); //client writes here/server reads
        dos = new DataOutputStream(s.getOutputStream()); //opposite

        //sendMessage("i really hope this works \n");
        //reciveMessage();

        return s;
    }

    public String reciveMessage() throws IOException{

        String jesus = br.readLine();
        System.out.println(jesus);
        return jesus;
    }

    public void sendMessage(String string) throws IOException{

        dos.writeBytes(string);
        System.out.println("just sent "+string);
    }
}