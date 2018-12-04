# Peer to Peer Chaching Service (Group 11)
=======

## User guide:
* Each component has it's own directory. 
* In order to use the solution on one machine, open a new terminal in each of the directories
* The java files have been pre-compiled for ease of use, however if you wish to recompile, simply use the command "javac *.java" in each terminal
* ExternalServer's directory contains a sample file for testing, however it will work with any file type
* If you wish to test with more Clients than Client directories we have provided, simply create a new folder, copy into it Client.java, open a terminal and compile with "javac *.java"

## Execution:
* The components should be executed in this order: ExternalServer, Server, Client(s)
* ExternalServer
    * java ExternalServer portNumber
        * portNumber is the port on which you wish to run ExternalServer
* Server
    * note: Server will run on port 6968 by default (Client source code is consistent with this)
    * java Server serverIP portNumber
        * serverIP is the IP address of the machine running ExternalServer ("localhost" is sufficient if running locally)
        * portNumber is the same port you chose for ExternalServer
* Client
    * java Client cacheIP fileName
        * cacheIP is the IP address of the machine running Server ("localhost" is sufficient if running locally)
        * fileName must be the name of a file hosted in ExternalServer's directory

## Additional Notes:
### Limitations:
* file size is limited, the solution has been confirmed to work with files of up to a 98.1 MB .mp4 file
    * this is not a limitation of the proposed solution, just of this specific implementation
* Only one instance of a client will be able to host on a sinle machine at a time
    * This is due to using a hardcoded port # when a client begins hosting
    * Again this is a limitation of this specific implementation. With more complex messaging procedure, variable port numbers could be used
    * This should only come up if two instances of a client are both the first to request two different files from the cache server
        * the result will be that the second client will successfully download the file, then will throw "java.net.BindException: Address already in use" and exit


### Resources:

* For the sake of full transparency, the following links were used as guides to get started. However it should clear that they were just that: a starting point.

* https://www.javaworld.com/article/2075440/core-java/develop-a-generic-caching-service-to-improve-performance.html

* http://www.java2s.com/Code/Java/Network-Protocol/Asimpleproxyserver.htm

