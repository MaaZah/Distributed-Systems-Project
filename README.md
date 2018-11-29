Distributed-Systems-Project
=======

## Resources:

https://www.javaworld.com/article/2075440/core-java/develop-a-generic-caching-service-to-improve-performance.html

http://www.java2s.com/Code/Java/Network-Protocol/Asimpleproxyserver.htm


## Current Status

* ExternalServer sends memes.docx to whatever client connects to it
* Client currently only reads a file directly from ExternalServer
* Server currently acts as a proxy only for a specific host:port
* Server currently intercepts requests from client, checks cache, if not in cache, forward to server, else reply to client with "local available"


## TODO:
* MAKE SURE IT WILL WORK ACROSS DIFFERENT MACHINES
* ExternalServer:
    * Look into using HTTP server
    * Find solution for sending large files (java.lang.OutOfMemoryError: Java heap space)
    * DONE >>> Receive input from client for the filename
* Client:
    * Add simple UI to select from list of file names
    * DONE >>> Once file is downloaded, start a thread running code similar to that of ExternalServer
    * DONE >>> rewrite to send a string request and expect a string reply
        * DONE >>> depending on reply, either expect a file download from server or a host:port to download from
* Server: 
    * DONE >>> add functionality to read file from ExternalServer and forward to client
    * DONE >>> only read reply from server when cache reply is null

