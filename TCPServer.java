/*
 * Authors : Anand Kumar Dharmaraj (800867560), Varun Varma Sangaraju (800859717)
 */


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TCPServer {

    // first argument to main method is a socket number to be provided for the host to run the service
    public static void main(String[] args) {
        
        int serport=Integer.parseInt(args[0]);
        try
        {
            // a server socket is opened to start listening to incoming requests
            ServerSocket sersoc = new ServerSocket(serport);
            System.out.println("Accepting Connections!");
            // initial sequence number of response messages is set to 100
            // scount holds the sequence number of the next packet to be sent
            int scount=100;
            // server runs until manually terminated
            while(true)
            {
                
                // listen and accept request for connections
                Socket connectionSocket = sersoc.accept();
                System.out.println("Client Connected");
                
                // initialize input and output streams as required
                InputStream is = connectionSocket.getInputStream();                
                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
               
                // extract the bufferSize of data sent by client in the connection
                int bufferSize = connectionSocket.getReceiveBufferSize();
                // initializing a bytes array to store incoming data
                byte[] bytes = new byte[bufferSize];
                System.out.println("Receiving file size: " + bytes.length);
                
                // receive the file in bytes array
                int count;
                while ((count = is.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                out.flush();
                
                // prepare a response message and store in resparray
                byte[] seq=Arrays.copyOfRange(bytes, 0, 2);
                byte[] msg=Arrays.copyOfRange(bytes, 2, bytes.length);                
                String seqno="";
                // sequence number of response message is found from scount variable
                scount=scount+1;
                seqno=seqno+Integer.toBinaryString(scount);
                //System.out.println("Seqno is: "+seqno);                
                short a = Short.parseShort(seqno, 2);
                ByteBuffer temp = ByteBuffer.allocate(2).putShort(a);                
                byte[] pktseq = new byte[2];
                pktseq=temp.array();
                byte[] resparray=new byte[pktseq.length+msg.length];    
                System.arraycopy(seq, 0, resparray, 0, pktseq.length);       
                System.arraycopy(msg, 0, resparray, pktseq.length, msg.length);
                
                // initialize a different input stream to extract data from resparray and output stream to send data
                InputStream isx = new ByteArrayInputStream(resparray);
                BufferedOutputStream bos = new BufferedOutputStream(connectionSocket.getOutputStream());
                
                // send the resparray data to client through the connected socket                
                int count2;                
                while ((count2 = isx.read(resparray)) > 0) {
                    bos.write(resparray, 0, count2);
                }                
                bos.flush();
                // shut down output as a notification of all output being sent
                connectionSocket.shutdownOutput();
                // close the client socket connection
                connectionSocket.close();
            }
            // close the server socket once manually terminated
            sersoc.close();
        }
        // catch all exceptions from the above actions and report it here
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
