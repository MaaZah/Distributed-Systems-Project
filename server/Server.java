//A proxy server that stores a chache of client IP/request pairs
//that handles/forwards requests according to what is present in the cache.

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

    private static CacheManager cache;
    

    public static void main(String[] args) throws IOException{

        //check for correct number of arguments
        if(args.length < 2){
            System.out.println("Proper usage: java Server serverIP serverPort");
            System.exit(1);
        }

        //set IP and port for the file server for which this server is a proxy
        String host = args[0];
        int remotePort = Integer.parseInt(args[1]);

        //initialize the cacheManager, set local port number, and call the runServer function
        try{
            cache = new CacheManager();
            int localPort = 6968;
            System.out.println("Starting proxy for " + host + ":" + remotePort + " on port " + localPort);

            
            //open server socket on localport (6968)
            ServerSocket ss = new ServerSocket(localPort);

            while(true){
                System.out.println("***********");

                //null sockets
                Socket client = null;
                Socket server = null;
                
                //wait for a new client
                client = ss.accept();
                DataInputStream din = new DataInputStream(client.getInputStream());
                DataOutputStream dout = new DataOutputStream(client.getOutputStream());

                //get client address and reformat
                String address = client.getRemoteSocketAddress().toString();
                address = address.substring(0, address.indexOf(":"));
                address = address.replaceAll("^/+", "");
                System.out.println("New Client: " + address);

                String req = din.readUTF();

                //check to see if client sent shutdown key indicating the client has closed.
                if(req.equals("2cf94351-10f1-4b05-8de9-b47ec950eb76")){
                    System.out.println("shutting down client");
                    
                    //read corresponding request string from client
                    String sdreq = din.readUTF();

                    //remove said request from cache to avoid forwarding any future clients to the dead client
                    cache.removeByID(sdreq);

                    //close client socket, skip the rest of the loop.
                    din.close();
                    client.close();
                    continue;
                }
                boolean flag;
                CacheableObject cached;
                String peerAddress = "";
                int peerPort = 0;
                //check to see if request is present in cache
                if((cached = (CacheableObject)cache.getFromCache(req)) == null){
                    flag = false;
                    //add request to cache along with client's IP, to expire in 15 minutes
                    cached = new CacheableObject(address, req, 5 );
                    cache.addToCache(cached);
                }else{
                    flag = true;
                    //cached = (CacheableObject)cache.getFromCache(req);

                    //get the hosting-client's IP
                    peerAddress = (String)cached.getObject();
                    System.out.println("Peer Address: " + peerAddress);
                    peerPort = 6967;
                }

                //start new thread to handle client
                Thread t = new runServer(client, dout, din, req, flag, host, remotePort, peerAddress, peerPort);
                t.start();

            }
        }catch(Exception e){
            System.err.println(e);
            e.printStackTrace();
        }
    }
    
}

//thread to handle a client
class runServer extends Thread{
    public Socket client;
    public DataInputStream din;
    public DataOutputStream dstc;
    public DataInputStream streamFromServer;
    private boolean flag = false;
    public String peerAddress;
    public int peerPort;
    public String address;
    public DataOutputStream streamToServer;
    private String req;
    private String host;
    private int remotePort;
    
    
    public runServer(Socket client, DataOutputStream dos, DataInputStream din, String r, boolean f, String h, int p, String pa, int pp){
        this.client = client;
        this.dstc = dos;
        this.din = din;
        this.flag = f;
        this.req = r;
        this.host = h;
        this.remotePort = p;
        this.peerAddress = pa;
        this.peerPort = pp;
    }

    public void run(){
        Socket server = null;

        //temporary object of type CacheableObject
        CacheableObject cached;
        try{
            //read string from client
            
            if(!flag){
                //if request is not present in the cache, we must forward the request to the file server
                try{
                    //open a socket to the file server for which this server is a proxy
                    server = new Socket(host, remotePort);

                }catch(IOException e){
                    System.err.println("Cannot connect to " + host + ":" +remotePort + " " +e);
                    e.printStackTrace();
                    client.close();
                    return;
                }
                
                //open server i/o streams
                streamFromServer = new DataInputStream(server.getInputStream());
                streamToServer = new DataOutputStream(server.getOutputStream());

                //forward request string to file server
                streamToServer.writeUTF(req);
                streamToServer.flush();
            }
            
        }catch(IOException e){
            System.err.println(e);
            e.printStackTrace();
        }
        
        if(!flag){
            //if request was not present in the cache, we must get the file from the file server
            try{
                
                //read filename and file length from the file server
                String fileName = streamFromServer.readUTF();
                long size = streamFromServer.readLong();

                //write filename and filesize to client
                dstc.writeUTF(fileName);
                dstc.writeLong(size);

                //read entire file from file server and write it to client
                byte[] buffer = new byte[1024];
                int bytesRead;
                while (size > 0 && (bytesRead = streamFromServer.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
                {
                    dstc.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                }

                //close streams
                dstc.close();
                streamFromServer.close();
                streamToServer.close();
            }catch(IOException e){
                System.err.println(e);
                e.printStackTrace();
            }
        }else if(flag){
            //if the request was present in the cache, we must forward hosting-client information to new client
            try{
                //write key-string to tell client that the file is hosted on local network by another client
                dstc.writeUTF("e35b4c8d-5cfd-4703-b733-554134897799");

                //write hosting-client IP/port to client
                dstc.writeUTF(peerAddress);
                dstc.writeInt(peerPort);

                //close socket
                dstc.close();
            }catch(Exception e){
                System.err.println(e);
                e.printStackTrace();
            }
        }
    }
}