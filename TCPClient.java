/*
 * Authors : Anand Kumar Dharmaraj (800867560), Varun Varma Sangaraju (800859717)
 */


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TCPClient {

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
    // fourth argument is the number of packets to be sent (n=1000 for 1000 packets to be sent)
    // fifth argument is the number of iterations to perform for evaluation
    public static void main(String[] args) {
        
        String sentence;        
        try
        {
                        
            int n = Integer.parseInt(args[3]);
            
            
            
            int iterations = Integer.parseInt(args[4]);
            
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
                for(int i=1;i<=n;i++)
                {
                    String host=args[0];
                    int port= Integer.parseInt(args[1]);
                    // non-persitent connection to host with port number given as arguments
                    Socket clientSocket = new Socket(host, port);
                    // initialize required input and output streams to send and receive data
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));                
                    BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());

                    // form the packet to be sent with sequence number and character string
                    String seqno="";
                    seqno=seqno+Integer.toBinaryString(i);
                    System.out.println("Seqno is: "+seqno);
                    short a = Short.parseShort(seqno, 2);
                    ByteBuffer bytes = ByteBuffer.allocate(2).putShort(a);
                    // sequence number byte array
                    byte[] array = new byte[2];
                    array=bytes.array();                
                    sentence = formString(Integer.parseInt(args[2]));
                    // message byte array
                    byte[] array2=sentence.getBytes();
                    // combine both arrays                
                    byte[] sendarray=new byte[array.length+array2.length];
                    System.arraycopy(array, 0, sendarray, 0, array.length);
                    System.arraycopy(array2, 0, sendarray, array.length, array2.length);
                    //System.out.println("Send Byte Array formed..");

                    // output stream to send data
                    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(sendarray));

                    // set start timer                
                    double sendtime=System.currentTimeMillis();
                    //System.out.println(sendtime);

                    // send byte data
                    int count;
                    while ((count = bis.read(sendarray)) > 0) {
                        out.write(sendarray, 0, count);
                    }
                    out.flush();

                    // shutdown output to notify output stream ending
                    clientSocket.shutdownOutput();

                    // prepare arrays for receiving data
                    int bufferSize = clientSocket.getReceiveBufferSize();
                    byte[] bytes2 = new byte[bufferSize];
                    //System.out.println("Receiving file size: " + bytes2.length);

                    // input stream to receive data
                    InputStream xo = clientSocket.getInputStream();

                    xo.read(bytes2);

                    //System.out.println("Received File..");

                    // note end time or receive time
                    double receivetime=System.currentTimeMillis();
                    //System.out.println(receivetime);

                    //Add the time difference to totaltime to aggregate calculations
                    totaltime[k]=totaltime[k]+(receivetime-sendtime);

                    double RTT = receivetime-sendtime;
                    if(maxRTT[k]<RTT)
                    {
                        maxRTT[k]=RTT;
                    }

                    // close the connection
                    clientSocket.close();
                }
            
                // print necessary values
                /*
                System.out.println("The total time for 1000 packets: "+totaltime);  
                double avgRTT = totaltime[k]/n;
                double avgETE = avgRTT/2;
                System.out.println("Average ETE is: "+avgETE);
                double maxETE = maxRTT[k]/2;
                System.out.println("Maximum ETE is: "+maxETE);
                */
            }
            // avgETE has the average ETE for each iteration
            double[] avgETE = new double[iterations];
            for(int j=0;j<iterations;j++)
            {
                avgETE[j]=(totaltime[j]/(2*n));
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
            }            
        }        
        // all exceptions are caught and reported here
        catch(Exception e)
        {
            System.out.println(e);
        }
    }   
}
