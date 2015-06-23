/*
 * Authors : Anand Kumar Dharmaraj (800867560), Varun Varma Sangaraju (800859717)
 */


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketPgmgServer {

    //  buffer size is set here
    static int BUFFER_SIZE=100;
    // main method gets port to run service
    public static void main(String[] args) {
        
        // port to run service
        int serport=Integer.parseInt(args[0]);
        
        // initialise a TCP server socket and client socket
        ServerSocket sersoc;
        Socket client;
        
        try
        {
            // create server socket
            sersoc = new ServerSocket(serport);
            
            // until server is munally terminated run the following
            while(true)
            {
                System.out.println("Accepting connections..");
                
                // listen and accept to client connection request
                client = sersoc.accept();
                
                // initialize streams for getting requests
                InputStream is = client.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                
                // read request
                String message = reader.readLine();
                
                String[] msgarray=message.split(" ");
                
                // for PUT command
                if(msgarray[0].equals("PUT"))
                {
                    // extract buffer size of client side socket 
                    int bufferSize = client.getReceiveBufferSize();
                    
                    // any access control can be checked here
                    // Access Control Rule Checking
                    
                    // set up file write streams
                    FileOutputStream fos = new FileOutputStream("test.txt");                
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    byte[] bytes = new byte[bufferSize];
                    System.out.println("Receiving file size: " + bytes.length);
                    
                    // write data to file
                    int count;

                    while ((count = is.read(bytes)) > 0) {
                        bos.write(bytes, 0, count);
                    }
                    bos.flush();
                    System.out.println("File Created..");

                    // stream to send response
                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                    out.flush();
                    // send response
                    out.writeObject("200 OK File Created");

                    bos.close();
                    is.close();
                    
                    // close client connection
                    client.close();
                }
                // for GET command
                else if(msgarray[0].equals("GET"))
                {
                    // stream to send response
                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                    out.flush();                    
                    try
                    {
                        // try creating file object
                        File file = new File(msgarray[1]);

                        PrintWriter pw=new PrintWriter(new BufferedOutputStream(out));
                        // create response message
                        String req = "200 OK "+msgarray[1]+" available / HTML/1.1\r\n\r\n";
                        pw.println(req);
                        pw.flush();
                        // set input stream to use file data
                        FileInputStream fis = new FileInputStream(file); 
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        byte[] buffer = new byte[(int)(file.length())];  
                        System.out.println("Sending file size: "+buffer.length);
                        // send file
                        int bytesRead = 0;  
                        while ((bytesRead = bis.read(buffer)) > 0) {
                                out.write(buffer, 0, bytesRead);
                                out.flush();          
                        }
                        System.out.println("File sent..");
                    }
                    // catch file not found exceptions and report them here
                    catch(Exception e)
                    {
                        out.writeObject("404 File Not Found with message - "+e);
                        out.close();
                    }
                    // close the client socket connection
                    client.close();
                }
            }
            // close the server socket once manually terminated
            sersoc.close();
        }
        // catch all exceptions from the above actions and report it here
        catch (Exception e) {
           System.out.println(e);
        }
    }    
}
