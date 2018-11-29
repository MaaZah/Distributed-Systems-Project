import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

    private static CacheManager cache;
    public static InputStream streamFromServer;
    public static boolean flag = false;
    public static String peerAddress;
    public static int peerPort;
    public static String address;

                
    public static OutputStream streamToServer;
    public static void main(String[] args) throws IOException{

        try{
            cache = new CacheManager();
            String host = "localhost";
            int remotePort = 6969;
            int localPort = 6968;

            System.out.println("Starting proxy for " + host + ":" + remotePort + " on port " + localPort);
            runServer(host, remotePort, localPort);
        }catch(Exception e){
            System.err.println(e);
        }
    }


    public static void runServer(String host, int remotePort, int localPort) throws IOException
    {
        ServerSocket ss = new ServerSocket(localPort);

        final byte[] request = new byte[1024];
        byte[] reply = new byte[4096];

        while(true){
            Socket client = null;
            Socket server = null;
            
            try{
                client = ss.accept();
                address = client.getRemoteSocketAddress().toString();
                address = address.substring(0, address.indexOf(":"));
                address = address.replaceAll("^/+", "");
                System.out.println(address);
                final InputStream streamFromClient = client.getInputStream();
                final OutputStream streamToClient = client.getOutputStream();

                try{
                    server = new Socket(host, remotePort);

                }catch(IOException e){
                    PrintWriter out = new PrintWriter(streamToClient);
                    out.print("Proxy server cannot connect to " + host + ":" + remotePort + "\n" + e);
                    out.flush();
                    client.close();
                    continue;
                }

                streamFromServer = server.getInputStream();
                
                streamToServer = server.getOutputStream();

                // Thread t = new Thread(){
                //     public void run(){
                        
                       
                //     }
                // };

                // t.start();
                int bytesRead;
                CacheableObject cached;
                try{
                    bytesRead = streamFromClient.read(request);
                    //if request is not cached, cache it and forward request to server
                    if(cache.getFromCache(request) == null){
                        
                        flag = false;
                    }else{
                        flag = true;
                    }
                    if(!flag){
                        cached = new CacheableObject(address, request, 0 );
                        cache.addToCache(cached);
                        streamToServer.write(request, 0, bytesRead);
                        streamToServer.flush();
                    }else{
                        System.out.println("else");
                        cached = (CacheableObject)cache.getFromCache(request);
                        peerAddress = (String)cached.object;
                        System.out.println(peerAddress);
                        peerPort = 6967;
                        
                    }
                    
                }catch(IOException e){

                }
                
                if(!flag){
                    try{
                        System.out.println("IN IF");
                        DataInputStream dsfs = new DataInputStream(streamFromServer);
                        DataOutputStream dstc = new DataOutputStream(streamToClient);
                        String fileName = dsfs.readUTF();
                        long size = dsfs.readLong();

                        dstc.writeUTF(fileName);
                        dstc.writeLong(size);
                        byte[] buffer = new byte[1024];
                        while (size > 0 && (bytesRead = dsfs.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
                        {
                            dstc.write(buffer, 0, bytesRead);
                            size -= bytesRead;
                        }
                        dsfs.close();
                        dstc.close();
                    }catch(IOException e){

                    }
                    streamToClient.close();
                    streamToServer.close();
                }else if(flag){
                    System.out.println("IN ELSE");
                    streamToServer.close();
                    DataOutputStream dstc = new DataOutputStream(streamToClient);
                    dstc.writeUTF("e35b4c8d-5cfd-4703-b733-554134897799");
                    dstc.writeUTF(peerAddress);
                    dstc.writeInt(peerPort);
                    dstc.close();
                    streamToClient.close();

                }
                System.out.println("AFTER ELSE");

            }catch(IOException e){
                System.err.println(e);
            }finally{
                try{
                    if(server != null){
                        server.close();
                    }
                    if(client != null){
                        client.close();
                    }
                }catch(IOException e){

                }
            }
        }
    }
    
}