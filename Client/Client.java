import java.net.*;
import java.util.Scanner;
import java.io.*;
 
public class Client {
     
    private static boolean flag = false;
    private static String address;
    private static String request;
    private static int port;
    public static void main(String[] args) throws IOException {
 
        if(args.length < 2){
            System.out.println("Proper usage: java Client serverIP fileName");
            System.exit(1);
        }

        address = args[0];
        request = args[1];
        // Scanner sc = new Scanner(System.in);
        // System.out.println("Please choose a file: ");
        // System.out.println("1: memes.docx");
        // System.out.println("2: Veedeo(final)v4.2-last-finished-v5-done.mp4");
        // int input = sc.nextInt();
        // sc.close();
        // if(input == 1){
        //     System.out.println("1");
        //     request = "memes.docx";
        // }else if(input ==2){
        //     System.out.println("2");
        //     request = "Veedeo(final)v4.2-last-finished-v5-done.mp4";
        // }else{
        //     System.out.println("invalid input, exiting");
        //     System.exit(1);
        // }

        port = 6968;
        Socket sock = new Socket(address, port);
 
        int bytesRead;
        int current = 0;
         
        InputStream in = sock.getInputStream();
        DataOutputStream out = new DataOutputStream(sock.getOutputStream());
        out.writeUTF(request);

        DataInputStream din = new DataInputStream(in);
        String fileName = din.readUTF();
        System.out.println(fileName);
        System.out.println("***********");
        if(fileName.equals("e35b4c8d-5cfd-4703-b733-554134897799")){
            flag = true;
            String newHost = din.readUTF();
            int newPort = din.readInt();
            System.out.println(newHost);
            System.out.println(newPort);
            in.close();
            out.close();
            din.close();
            sock.close();
            sock = new Socket(newHost, newPort);
            in = sock.getInputStream();
            out = new DataOutputStream(sock.getOutputStream());
            din = new DataInputStream(in);
            out.writeUTF(request);
            fileName = din.readUTF();

        }
        OutputStream output = new FileOutputStream(fileName);
        long size = din.readLong();
        byte[] buffer = new byte[1024];
        while (size > 0 && (bytesRead = din.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
        {
            output.write(buffer, 0, bytesRead);
            size -= bytesRead;
        }
         
        output.close();

        if(!flag){
            Thread t = new Thread(){
                public void run(){

                    Runtime.getRuntime().addShutdownHook(new Thread() {
                        public void run() {
                            System.out.println("shutting down");
                            try{
                                Socket shutdownSocket = new Socket(address, port);
                                InputStream sdin = shutdownSocket.getInputStream();
                                DataOutputStream sdout = new DataOutputStream(shutdownSocket.getOutputStream());
                                sdout.writeUTF("shutdown");
                                sdout.flush();
                                sdout.writeUTF("memes.docx");
                                sdout.flush();
                                sdout.close();
                                shutdownSocket.close();  
                            }catch(Exception e){
                                System.out.println("shutdown failed");
                            } 

                        }       
                    });
                    int bytesRead;
                    int current = 0;
                    byte[] request = new byte[1024];
                
                    ServerSocket serverSocket = null;
                    try{
                        System.out.println("Hosting");
                        serverSocket = new ServerSocket(6967);
                        
                        while(true) {
                            System.out.println("************");
                            Socket clientSocket = null;
                            clientSocket = serverSocket.accept();
                            System.out.println("client accepted");
                            InputStream in = clientSocket.getInputStream();
                            bytesRead = in.read(request);
                            String requestString = new String(request);
                            requestString = requestString.trim();
                            System.out.println(requestString);
                            
                            // Writing the file to disk
                            // Instantiating a new output stream object
                            
                
                            //TODO: read input from client, and use that for filename.
                
                            File myFile = new File(requestString);
                            byte[] mybytearray = new byte[(int) myFile.length()];
                            
                            FileInputStream fis = new FileInputStream(myFile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            DataInputStream dis = new DataInputStream(bis);
                            dis.readFully(mybytearray, 0, mybytearray.length);
                
                            OutputStream os = clientSocket.getOutputStream();
                
                            DataOutputStream dos = new DataOutputStream(os);
                            dos.writeUTF(myFile.getName());
                            dos.writeLong(mybytearray.length);
                            dos.write(mybytearray, 0, mybytearray.length);
                        
                            dos.flush();
                            
                            clientSocket.close();
                        }
                    }catch(Exception e){

                        System.out.println("rip the dream");
                    }
                }
            };
            t.start();
        }
    }
}