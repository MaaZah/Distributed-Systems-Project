import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
 
public class Client {
     
    private static boolean flag = false;
    private static String address;
    private static String request;
    private static int port;
    public static void main(String[] args) throws IOException {
 
        //verify number of arguments
        if(args.length < 2){
            System.out.println("Proper usage: java Client serverIP fileName");
            System.exit(1);
        }

        //set address & request strings
        address = args[0];
        request = args[1];

        //cache server runs on port 6968
        port = 6968;

        //open socket to cache server
        Socket sock = new Socket(address, port);
 
        //open i/o streams
        InputStream in = sock.getInputStream();
        DataOutputStream out = new DataOutputStream(sock.getOutputStream());
        DataInputStream din = new DataInputStream(in);

        //write request to cache server
        out.writeUTF(request);

        //read filename from cache server
        String fileName = din.readUTF();
        System.out.println(fileName);
        System.out.println("***********");

        //check fileName against the "client hosted on local network" key
        if(fileName.equals("e35b4c8d-5cfd-4703-b733-554134897799")){
            flag = true;

            //read the IP:port of the client hosting the file
            String newHost = din.readUTF();
            int newPort = din.readInt();

            //close cache server socket
            in.close();
            out.close();
            din.close();
            sock.close();

            //open new socket to hosting-client
            sock = new Socket(newHost, newPort);
            System.out.println("Connecting to: " + newHost + ":" + newPort);

            //open i/o streams
            in = sock.getInputStream();
            out = new DataOutputStream(sock.getOutputStream());
            din = new DataInputStream(in);

            //write request to hosting-client
            out.writeUTF(request);

            //read fileName from hosting-client
            fileName = din.readUTF();

        }

        //open a FileOutPutStream where the file will be saved
        OutputStream output = new FileOutputStream(fileName);

        //read filesize from hosting-client
        long size = din.readLong();

        //read file from hosting-client and write to fileoutputstream
        int bytesRead;
        byte[] buffer = new byte[1024];
        while (size > 0 && (bytesRead = din.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
        {
            output.write(buffer, 0, bytesRead);
            size -= bytesRead;
        }
         
        //close socket
        output.close();
        sock.close();

        if(!flag){
            //if the file was retreived from the external file server and not another client, start hosting the file
            ServerSocket serverSocket = null;
            System.out.println("Hosting");
            serverSocket = new ServerSocket(6967);

            //set socket timeout to 5min, if no client connects in 5min it will throw exception and shut down
            serverSocket.setSoTimeout(5*60*1000);

            //add shutdown hook to notify cache server that this client is no longer hosting the file
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("shutting down");
                    try{
                        //Open socket to cache server
                        Socket shutdownSocket = new Socket(address, port);
                        
                        //create i/o streams
                        InputStream sdin = shutdownSocket.getInputStream();
                        DataOutputStream sdout = new DataOutputStream(shutdownSocket.getOutputStream());

                        //write shutdown key to cache server
                        sdout.writeUTF("2cf94351-10f1-4b05-8de9-b47ec950eb76");
                        sdout.flush();

                        //write request to cache so it knows what to remove
                        sdout.writeUTF(request);

                        //close socket
                        sdout.flush();
                        sdout.close();
                        shutdownSocket.close();  
                    }catch(Exception e){
                        System.err.println(e);
                        System.out.println("shutdown failed");
                    } 
                }       
            });
            int numClients = 0;
            ArrayList<Thread> threads = new ArrayList<Thread>();
            while(true) {
                try{
                    //wait for a client to connect
                    Socket clientSocket = null;
                    
                    clientSocket = serverSocket.accept();
                    numClients++;
                    System.out.println("client accepted");

                    //open i/o streams
                    OutputStream cos = clientSocket.getOutputStream();
                    DataOutputStream cdos = new DataOutputStream(cos);
                    DataInputStream cdin = new DataInputStream(clientSocket.getInputStream());

                    //Create new hosting thread and start it
                    Thread t = new fileHostingThread(clientSocket, cdos, cdin);
                    t.start();
                    threads.add(t);
                    System.out.println("************");
                    if(numClients ==5){
                        try{
                            for(int i=0;i<threads.size();i++){
                                threads.get(i).join();
                            }
                            System.exit(1);
                        }catch(Exception e){
                            System.err.println(e);
                            e.printStackTrace();
                        }
                    }
                }catch(SocketTimeoutException e){
                    System.out.println("Socket timed out after 5 minutes");
                    System.exit(1);
                }
            }
        }
    }
}

class fileHostingThread extends Thread{
    private Socket clientSocket;
    private DataOutputStream dos;
    private DataInputStream in;

    public fileHostingThread(Socket clientSocket, DataOutputStream dos, DataInputStream in){
        this.clientSocket = clientSocket;
        this.dos = dos;
        this.in = in;

    }

    public void run(){
        try{
            //read requested filename from client
            String requestString = in.readUTF();
            System.out.println("request recieved for: " + requestString);

            //open requested File and read into bytearray
            File myFile = new File(requestString);
            byte[] mybytearray = new byte[(int) myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);
            
            //write file name, file size, then file to client
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            
            //close socket/fileStream
            fis.close();
            clientSocket.close();
            
        }catch(Exception e){
            System.err.println(e);
        }
    }
}