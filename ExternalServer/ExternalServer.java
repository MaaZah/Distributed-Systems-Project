//A very simple file server

import java.net.*;
import java.io.*;
 
public class ExternalServer {
     
    public static void main(String[] args) throws IOException {
 
        //get port number on run
        if(args.length < 1){
            System.out.println("Proper usage: java ExternalServer portNumber");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        
     
        //open socket
        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(port);
        
        //infinite loop waiting for clients
        while(true) {

            //wait for client to connect & open i/o streams
            Socket clientSocket = null;
            clientSocket = serverSocket.accept();
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());

            //start a new thread to handle client
            Thread t = new handleClient(clientSocket, in, dos);
            t.start();
        }
    }
}

class handleClient extends Thread{
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream dos;

    public handleClient(Socket s, DataInputStream i, DataOutputStream o){
        this.clientSocket = s;
        this.in = i;
        this.dos = o;
    }

    public void run(){
        try{
            System.out.println("client accepted");
                
            //read request from client
            String request = in.readUTF();
            System.out.println("Request received: " + request);
                
            //open requested file and read into byte array
            File myFile = new File(request);
            byte[] mybytearray = new byte[(int) myFile.length()];
            
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);

            dis.readFully(mybytearray, 0, mybytearray.length);

            //open output stream to client and write the bytearray to client
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
                
            //close client socket and streams
            dis.close();
            clientSocket.close();
            System.out.println("****************");
        }catch(Exception e){
            System.err.println(e);
            e.printStackTrace();
        }
    }
}