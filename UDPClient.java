/*
 * Authors : Anand Kumar Dharmaraj (800867560), Varun Varma Sangaraju (800859717)
 */


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class UDPClient {
    
    // formString function forms a character string of required length given as argument and returns it
    public static String formString(int n)
    {
        String temp="";
        for(int i=0;i<n;i++)
        {
            temp=temp+'c';
        }
        return temp;
    }
    // main method performs most of the operations required, it takes the following arguments
    // first argument is the hostname/IP address and the second argument is the port number
    // takes length of character string to send at each connection as third argument 
    // fourth argument is the number of packets to be sent (n=1000 for 1000 packet/iterations to be sent/performed)
    // fifth argument is the number of iterations to perform for evaluation
    public static void main(String[] args) {

        String sentence;
        try
        {
            
            String serverHostname = new String (args[0]);
            int port = Integer.parseInt(args[1]);

             
            // resolve the IP address of the host
            InetAddress IPAddress = InetAddress.getByName(serverHostname);             
            System.out.println ("Attemping to connect to " + IPAddress +" via UDP port "+ port);    
            
            // no.of packets to send
            int n=Integer.parseInt(args[3]);
            
            
            
            int iterations = Integer.parseInt(args[4]);
            
            // losscount has the no.of lost packets
            int[] losscount= new int[iterations];
            for(int j=0;j<iterations;j++)
                losscount[j]=0;
            
            // stores max RTT of packets
            double[] maxRTT = new double[iterations];
            for(int j=0;j<iterations;j++)
                maxRTT[j]=Double.MIN_VALUE;
            
            // totaltime stores the total RTT time for all packets transmitted
            double[] totaltime= new double[iterations];
            for(int j=0;j<iterations;j++)
                totaltime[j]=0;
     
            // do for k iterations
            for(int k=0;k<iterations;k++)
            {
                // send n packets
                for(int i=1;i<=n;i++)
                {
                    // initialize a datagram socket to send and receive to UDP traffic
                    DatagramSocket clientSocket = new DatagramSocket();

                    // construct the packet and send
                    String seqno="";
                    seqno=seqno+Integer.toBinaryString(i);
                    //System.out.println("Seqno is: "+seqno);
                    short a = Short.parseShort(seqno, 2);
                    ByteBuffer bytes = ByteBuffer.allocate(2).putShort(a);
                    // byte array to store sequence number
                    byte[] array = new byte[2];
                    array=bytes.array();
                    // byte array to store message
                    sentence = formString(Integer.parseInt(args[2]));
                    byte[] array2=sentence.getBytes();
                    // combine the two arrays to generate the send/request message               
                    byte[] sendarray=new byte[array.length+array2.length];          
                    System.arraycopy(array, 0, sendarray, 0, array.length);
                    System.arraycopy(array2, 0, sendarray, array.length, array2.length);

                    // export the message into a 1024 byte array for sending
                    byte[] sendData = new byte[1024];
                    System.arraycopy(sendarray, 0, sendData, 0, sendarray.length);


                    // set start timer

                    double sendtime=System.currentTimeMillis();
                    //System.out.println(sendtime);

                    // send packet in bytes
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port); 
                    clientSocket.send(sendPacket); 

                    System.out.println("Packet Sent..");

                    // set up byte array to receive data
                    byte[] receiveData = new byte[1024];

                    // construct datagram packet to receive packet
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 

                    System.out.println ("Waiting for return packet");

                    // time out for waiting is 3 seconds (fixed)
                    clientSocket.setSoTimeout(3000);


                    try
                    {
                        // receive data from UDP scoket
                        clientSocket.receive(receivePacket); 

                        // get data and do processing if required
                        //String modifiedSentence = new String(receivePacket.getData()); 

                        // get ip address and port of parent / source 
                        InetAddress returnIPAddress = receivePacket.getAddress();
                        int pport = receivePacket.getPort();

                        System.out.println ("From server at: " + returnIPAddress +":" + pport);
                        //System.out.println("Message: " + modifiedSentence); 
                        System.out.println("Received File..");

                        // note receiving time
                        double receivetime=System.currentTimeMillis();


                        // update total ETE time
                        totaltime[k]=totaltime[k]+(receivetime-sendtime);
                        if(receivetime-sendtime>maxRTT[k])
                        {
                            maxRTT[k]=receivetime-sendtime;
                        }

                    }
                    catch (SocketTimeoutException ste)
                    {
                        losscount[k]=losscount[k]+1;
                     System.out.println ("Timeout Occurred: Packet assumed lost"+ste);
                    }        
                    //System.out.println("FROM SERVER: " + response);
                    clientSocket.close();

                }
                // print necessary values for analysis
                /*
                System.out.println("The total time for 1000 packets: "+totaltime);
                double averageETE = totaltime/((double)2*(double)(n-losscount));
                System.out.println("The average ETE is: "+averageETE);
                System.out.println("Loss count is: "+losscount);
                double maxETE = maxRTT/2;
                System.out.println("The max ETE is: "+maxETE);
                */
            }
            // close the client socket connection
           // avgETE has the average ETE for each iteration
            double[] avgETE = new double[iterations];
            for(int j=0;j<iterations;j++)
            {
                avgETE[j]=(totaltime[j]/(2*(n-losscount[j])));
            }
            // max ETE has the maximum ETE for each iteration
            double[] maxETE = new double[iterations];
            for(int j=0;j<iterations;j++)
            {
                maxETE[j]=maxRTT[j]/2;
            }
            // print necessary values
            for(int j=0;j<iterations;j++)
            {
                System.out.println("AvgETE for Iteration "+(j+1)+": "+avgETE[j]);
                System.out.println("MaxETE for Iteration "+(j+1)+": "+maxETE[j]);
                System.out.println("Loss Count for Iteration "+(j+1)+": "+losscount[j]);                
            }   
        }
        // all exceptions are caught and reported here
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
}
