import java.net.*;
import java.io.*;
 
public class Client {
     
    public static void main(String[] args) throws IOException {
 
        Socket sock = new Socket("localhost", 6969);
 
        int bytesRead;
        int current = 0;
         
        InputStream in = sock.getInputStream();

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
    }
}