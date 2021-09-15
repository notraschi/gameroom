package src2;
import java.net.*;
import java.io.*;

public class client {

    Socket ss;
    int portn;
    public String ip;

    DataInputStream dis;
    DataOutputStream dos;
    BufferedReader br;

    public Socket connect() throws UnknownHostException, IOException{

        ss = new Socket(ip, portn); //connects to the servers's socket

        System.out.println("client connected");

        //dis = new DataInputStream(ss.getInputStream()); //client reads here
        br = new BufferedReader(new InputStreamReader(ss.getInputStream()));
        dos = new DataOutputStream(ss.getOutputStream()); //client writes here

        return ss;
    }

    public void sendMessage(String s) throws IOException{

        dos.writeBytes(s);
        System.out.println("just sent "+s);
    }

    public String reciveMessage() throws IOException{

        String jesus = br.readLine();
        System.out.println(jesus);
        return jesus;
    }
}
