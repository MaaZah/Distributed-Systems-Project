import java.net.*;
import java.io.*;
 
public class ExternalServer {
     
    public static void main(String[] args) throws IOException {
 
        if(args.length < 1){
            System.out.println("Proper usage: java ExternalServer portNumber");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        
        int current = 0;
        String request;
     
        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(port);
        
           
        while(true) {
            Socket clientSocket = null;
            clientSocket = serverSocket.accept();
            System.out.println("client accepted");
             
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            request = in.readUTF();
            System.out.println(request);
             
            // Writing the file to disk
            // Instantiating a new output stream object
            

            //TODO: read input from client, and use that for filename.

            File myFile = new File(request);
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
            System.out.println("****************");
        }
    }
}