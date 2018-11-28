import java.net.*;
import java.io.*;
 
public class Client {
     
    public static void main(String[] args) throws IOException {
 
        Socket sock = new Socket("localhost", 6968);
 
        int bytesRead;
        int current = 0;
         
        InputStream in = sock.getInputStream();
        OutputStream out = sock.getOutputStream();
        out.write("memes.docx".getBytes());

        DataInputStream din = new DataInputStream(in);
        String fileName = din.readUTF();
        OutputStream output = new FileOutputStream(fileName);
        long size = din.readLong();
        byte[] buffer = new byte[1024];
        while (size > 0 && (bytesRead = din.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
        {
            output.write(buffer, 0, bytesRead);
            size -= bytesRead;
        }
         
        output.close();

        Thread t = new Thread(){
            public void run(){
                int bytesRead;
                int current = 0;
             
                ServerSocket serverSocket = null;
                try{
                    serverSocket = new ServerSocket(6967);
                    
                    while(true) {
                        Socket clientSocket = null;
                        clientSocket = serverSocket.accept();
                        
                        InputStream in = clientSocket.getInputStream();
                        
                        // Writing the file to disk
                        // Instantiating a new output stream object
                        
            
                        //TODO: read input from client, and use that for filename.
            
                        File myFile = new File("memes.docx");
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