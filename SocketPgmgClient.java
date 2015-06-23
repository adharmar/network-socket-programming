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
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetAddress;


public class SocketPgmgClient {

    //  buffer size is set here
    static int BUFFER_SIZE=100;
    // iptranslation takes host name as input and gives out a ip address
    public static String iptranslation(String host)
    {
        String ip="";
        try
        { 
            boolean isValid =  ipValid(host);
            if(isValid==true)
            {
                ip=host;
            }
            else
            {
                InetAddress inet = InetAddress.getByName(host);
                ip=inet.getHostAddress();
                
            }
        }
        catch(Exception e)
        {
            System.out.println("Cannot resolve hostname or "+e);
        }
        return ip;
    }
    // ipValid checks is given string ip is a valid IP address
    public static boolean ipValid(String ip)
    {
        try
        {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        String[] parts = ip.split( "\\." );
        if ( parts.length != 4 )
        {
            return false;
        }

        for ( String s : parts )
        {
            int i = Integer.parseInt( s );
            if ( (i < 0) || (i > 255) ) {
                return false;
            }
        }
        if(ip.endsWith(".")) {
                return false;
        }

        return true;
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
    }
    // main method takes 4 arguments: host, port, command, file (to get or put)
    public static void main(String[] args)
    {
        // get the host, port, cmd, filename
        String host=args[0];        
        int port=Integer.parseInt(args[1]);        
        String cmd=args[2];        
        String filename=args[3];
        // check if the command is valid
        if (!(cmd.equals("GET") || cmd.equals("PUT")))
        {
            System.out.println("Command Invalid!");
            System.exit(0);
        }
        
        System.out.println("Command Valid! Proceeding with connection!");
        
        try
        {
            // check ip address or name, if name convert to ip address
            String ip=iptranslation(host);
            //System.out.println("The IP to contact is: "+ip);
            
            // initialize socket for given ip (host) and port  
            Socket s = new Socket(ip,port);
            System.out.println("Connection Successful");
            
            // for GET command
            if(cmd.equals("GET"))
            {
                // initialise output writer
                PrintWriter out =new PrintWriter(s.getOutputStream(), true);
                
                // construct HTTP request
                String req = "GET "+filename+" / HTML/1.1\r\n\r\n";
                // String req = "GET / HTML/1.1\r\n\r\n";
                
                // send request
                out.println(req);
                out.flush();
                // shut down output to indicate termination of output from client side
                s.shutdownOutput();
                
                // initialise input and output streams for receiving data
                InputStream is = s.getInputStream();                
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                // read message from server
                String message = reader.readLine();
                System.out.println("Server: "+message);
                // set buffer to receive file
                int bufferSize = s.getReceiveBufferSize();
                System.out.println("Buffer size: " + bufferSize);
                // receive file
                try
                {
                    // streams for file writing
                    FileOutputStream fos = new FileOutputStream(filename);                
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    // byte array to output file
                    byte[] bytes = new byte[bufferSize];
                    // receive and write bytes to file
                    int count;
                    while ((count = is.read(bytes)) > 0) {
                        bos.write(bytes, 0, count);
                    }
                    System.out.println("File "+filename+" received..");
                    bos.flush();
                }
                // catch file write exceptions / socket write exceptions and report here
                catch(Exception e)
                {
                    System.out.println("File write error - "+e);
                }
                // close streams
                out.close();
                is.close();
                // close client socket connection
                s.close();
            }
            // for PUT command
            else if(cmd.equals("PUT"))
            {
                // output stream to send data
                BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
                
                // file object
                File file = new File(filename);
                
                PrintWriter pw=new PrintWriter(new BufferedOutputStream(out));
                // construct HTTP request
                String req = "PUT "+file.length()+" "+filename+" / HTML/1.1\r\n\r\n";
                // send request data
                pw.println(req);
                pw.flush();
                
                // streams to send file
                FileInputStream fis = new FileInputStream(file); 
                BufferedInputStream bis = new BufferedInputStream(fis);
                // byte array to store and send file
                byte[] buffer = new byte[(int)(file.length())];  
                // send file
                int bytesRead = 0;                 
                while ((bytesRead = bis.read(buffer)) > 0) {                  
                        out.write(buffer, 0, bytesRead);
                        out.flush();          
                }  
                // shutdown output to notify end of output
                s.shutdownOutput();
                
                // stream to read response from server
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                // print out response from server
                String responseLine;
                while ((responseLine = in.readLine()) != null)
                {
                    System.out.println("Server: " + responseLine);
                }
                
                // close streams
                out.close();
                fis.close();
                bis.close();
                // close the client socket connection
                s.close();
            }          
        }
        // catch all exceptions and report here
        catch(Exception e)
        {
            System.out.println(e);
        }
    }    
}
