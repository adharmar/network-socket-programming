/*
 * Authors : Anand Kumar Dharmaraj (800867560), Varun Varma Sangaraju (800859717)
 */


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UDPServer {

  
    // gets argument port number
    public static void main(String[] args) {
     
        int serport=Integer.parseInt(args[0]);
        try
        {           
            // initialize a datagram socket to send and receive to UDP traffic
            DatagramSocket sersoc = new DatagramSocket(serport);
            System.out.println("Accepting Connections!");           
            // until server is manually terminated do the following (accept connections, receive and send data)
            while(true)
            {
                // set up a byte array for sending and receiving data
                byte[] receiveData = new byte[1024];
                byte[] sendData = new byte[1024];
                
                // receive data from the socket
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                sersoc.receive(receivePacket);
                System.out.println("Client Connected");
                // get the data from packet
                String sentence = new String(receivePacket.getData()); 
                // get source address and source port
                InetAddress IPAddress = receivePacket.getAddress(); 
                int port = receivePacket.getPort(); 
  
                System.out.println ("From: " + IPAddress + ":" + port);
                //System.out.println ("Message: " + sentence);

                // do some calculations if required
                String capmsg = sentence.toUpperCase(); 
                
                sendData = capmsg.getBytes(); 
                // send the formed packet to source
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);   
                sersoc.send(sendPacket); 
            }
            // close socket once manually terminated
            sersoc.close();
        }
        // catch all exceptions and report them here
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
}
