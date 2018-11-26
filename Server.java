import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.sun.security.ntlm.Server;

public class Server{

    private static CacheManager cache;
    public static void main(String[] args) throws IOException{

        try{
            cache = new CacheManager();
            String host = "localhost";
            int remotePort = 6969;
            int localPort = 6968;

            System.out.println("Starting proxy for " + host + ":" + remotePort + " on port " + localPort);
            runServer(host, remotePort, localPort);
        }catch(Exception e){
            System.eer.println(e);
        }
    }


    public static void runServer(String host, int remotePort, int localPort) throws IOException
    {
        ServerSocket ss = new ServerSocket(localPort);

        final byte[] request = new byte[1024];
        byte[] reply = new byte[4096];

        while(true){
            Socket client = null, Server = null;
            try{
                client = ss.accept();
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

                final InputStream streamFromServer = server.getInputStream();
                final OutputStream streamToServer = server.getOutputStream();

                Thread t = new Thread(){
                    public void run(){
                        int bytesRead;
                        CacheableObject cached;
                        try{
                            while((bytesRead = streamFromClient.read(request)) != -1){
                               
                                //if request is not cached, cache it and forward request to server
                                if(cache.getFromCache(request) == null){
                                    cached = new CacheableObject(client.getRemoteSocketAddress().toString(), request, 0 );
                                    cache.addToCache(cached);
                                    streamToServer.write(request, 0, bytesRead);
                                    streamToServer.flush();

                                }else{
                                    //TODO: if request IS cached, send cached to client
                                    byte[] message = "local available".getBytes();
                                    streamToClient.write(message, 0, message.length );
                                    streamToClient.flush();
                                    streamFromClient.read();


                                }
                                
                            }
                        }catch(IOException e){

                        }
                        try{
                            streamToServer.close();
                        }catch(IOException e){

                        }
                    }
                };

                t.start();

                int bytesRead;
                try{
                    while((bytesRead = streamFromServer.read(reply)) != -1){
                        streamToClient.write(reply, 0, bytesRead);
                        streamToClient.flush();
                    }
                }catch(IOException e){

                }
                streamToClient.close();

            }catch(IOException e){
                System.err.println(e);
            }finally{
                try{
                    if(server != null){
                        Server.close();
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