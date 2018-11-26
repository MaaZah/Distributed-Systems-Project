import java.net.*;
import java.io.*;
 
public class ExternalServer {
     
    public static void main(String[] args) throws IOException {
 
        int bytesRead;
        int current = 0;
     
        ServerSocket serverSocket = null;
        serverSocket = new ServerSocket(6969);
           
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
    }
}