# network-socket-programming
This project implements 

Part - I: An HTTP client and server that run a simplified version of HTTP/1.1. Specifically, you will implement two HTTP commands: GET and PUT

Part - II: UDP vs. TCP - A Performance Evaluation 

Documentation:

Part – I
Instructions to execute submitted code:
Arguments for SocketPgmgClient
1.	Host
2.	Port
3.	Command
4.	File (to get or put)

Arguments for SocketPgmgServer
1.	Port number (to run service)

Working:
Initially, the server program (takes port number as argument) has to be executed to accept client connections. The server program might show a statement unreachable error on some compilers due to various standards. The server program needs to be executed forcibly in such situations. 
The client program gets four arguments as mentioned above. The following is the sequence of actions that happens once the two programs are executing.
•	Server accepts client connection
•	Client sends HTTP request
•	Server receives HTTP request, process request
•	If, command from client is a PUT request, client will send file after request
•	Server receives the file and stores in its location
•	Server responds with file created response
•	Client receives the response and displays it to user
•	If, command from client is a GET request, server will check if file is available
•	If, file is available it will send a response saying that the file is available
•	Server will send the file after that
•	Client receives and stores the file in client’s local directory
•	Client socket closes after server response
•	Server socket is open and wait for further connections
•	Server socket can be manually terminated if necessary
Output files with project folder: putfile is the client sending file for put command, index is file available in server for client get request, test is the server received file after put reuqest.


Part - II
Instructions to execute submitted code:
Arguments for TCPServer and UDPServer
1.	Port number (to run service)

Arguments for TCPClient and UDPClient
1.	Host
2.	Port number (same as service port number)
3.	Length of character string to be sent in each packet
4.	No. of packets to be sent in one iteration
5.	No. of iterations the packets need to be sent (for question 1 this value is 1, for question 2 value is 5)

To output the prints, the execution can be piped to a text file instead of printing in console. The below workings where recorded by piping the output to a text file.

Working:
Initially, the server program (takes port number as argument) has to be executed to accept client connections. The server program might show a statement unreachable error on some compilers due to varying standards. The server program needs to be executed forcibly in such situations.
The client program gets four arguments as mentioned above. The character string is of length 10 and should be mentioned in the arguments for execution. The following is the sequence of actions that happens once the two programs are executing.
•	Server accepts client connection
•	Client prepares and sends a TCP packet
•	Sending time is noted
•	Server receives the file and processes it if needed
•	Server sends back a TCP packet to the client
•	Client receives the server response and records receiving time
•	Difference between times is the RTT (Round Trip Time) between the two systems
•	Client closes the socket connection (non-persistent)
•	The above process is repeated for 1000 packets to record the average and maximum ETE (End to End delay). ETE is half of RTT
•	The entire process is repeated for 5 times to record 5 values to answer question 2
•	The process is repeated with character string size of 100 and 1000 for the above process
•	A similar process is done for UDP using datagrams
•	A loss count for packets lost is also noted when packets 



The average ETE and maximum ETE values (all values in milliseconds) are reported as follows:
TCP
Character  Length (Data)	Packets	Iterations	Average ETE	Max ETE
10	1000	1	3.415	37.5

For 5 iterations, with 10 character string and 1000 packets to be sent, the following values were recorded:
Iteration No.	Average ETE	Max ETE
1	3.368	51.5
2	3.35	51.0
3	2.978	55.0
4	3.3225	28.5
5	3.4675	38.5
	
For 5 iterations, with 200 character string and 1000 packets to be sent, the following values were recorded:
Iteration No.	Average ETE	Max ETE
1	4.1715	74.5
2	4.1895	43.0
3	4.4655	51.0
4	4.5205	41.0
5	3.9555	136.0
	
For 5 iterations, with 1000 character string and 1000 packets to be sent, the following values were recorded:
Iteration No.	Average ETE	Max ETE
1	4.544	163.0
2	5.835	78.0
3	6.39	172.5
4	4.585	45.0
5	6.3485	1723.5





	
UDP
The following values where recorded with a timeout of 3000ms (3 secs).
Character  Length (Data)	Packets	Iterations	Average ETE	Max ETE	Packets Lost
10	1000	1	3.3485	70.0	0

For 5 iterations, with 10 character string and 1000 packets to be sent, the following values were recorded:
Iteration No.	Average ETE	Max ETE	Packets Lost
1	4.4315	52.0	0
2	3.977	75.0	0
3	4.5475	830.0	1
4	5.7525	89.0	0
5	3.993	43.5	0
	
For 5 iterations, with 200 character string and 1000 packets to be sent, the following values were recorded:
Iteration No.	Average ETE	Max ETE	Packets Lost
1	4.564	54.0	0
2	3.442	50.0	0
3	3.5575	44.0	0
4	5.003	73.0	1
5	2.8985	36.0	0
	
For 5 iterations, with 1000 character string and 1000 packets to be sent, the following values were recorded:
Iteration No.	Average ETE	Max ETE	Packets Lost
1	4.2225	89.5	0
2	3.773	65.0	1
3	4.233	79.0	1
4	8.853	116.0	2
5	4.986	91.5	2
	





1)	Are the average ETE values, obtained from step 2, different for each of the 5 times that step 1 is repeated? Why?
Yes. The values are basically different due to the varying delays in connection setup, transmission, routing and forwarding and varying routing paths. But we can also see that the values are nearly same, meaning that the average ETE delay remains almost constant with a small difference (i.e near stable performance resulting in TCP reliability)

2)	Compare the average and maximum ETE for steps 2, 3, and 4 for TCP only. Explain the differences.
The average and maximum ETE for the steps 2, 3 and 4 for TCP are different. The difference is due to two reasons. Firstly, the data or payload is different in each step. We can also infer that the delay increases with increase in payload. Secondly, the routing paths taken by each packet will vary dynamically resulting in different routing paths and varying queuing delays. We can also infer that TCP performs reliably, (i.e. similar average and maximum ETE values for similar payloads) in terms of ETE.

3)	Compare the average and maximum ETE between TCP and UDP, for each of steps 2, 3, and 4. Explain the differences between TCP and UDP. What conclusion can you draw in terms of the performance of TCP and UDP?
When comparing the avgETE values between TCP and UDP, we can infer that UDP has smaller average ETE than TCP. But this inference is not entirely true. UDP may run into longer average ETE’s when the packets are routed via longer paths as UDP blindly sends packets without proper information about the receiver. This explains our unusual recordings in UDP average ETE values. TCP’s average ETE gradually increases with increase in payload size.
When comparing the maxETE values between TCP and UDP, we can infer that TCP gives a necessary increase in maxETE if payload is increased. In UDP, the maxETE’s are such that no assumptions or inferences can be drawn, making them random or independent with respect to UDP transmissions. This is mainly due to the uncertainty present in UDP where receiver’s information is not considered while routing packets.

4)	If the tests were run over the Internet, where there is a chance of packet loss, how would the ETE values be different between UDP and TCP?
The above tests were recorded over a WLAN and there were packet losses associated with it in UDP. In TCP, the packets did not suffer any loss. If these tests were run over the Internet, there is a chance of packet loss in both TCP and UDP. The ETE values for TCP will remain stable with steady increase with payload size increase. The ETE values of UDP will be quite smaller with unpredictable max delays and average delays for some packets due to its inherent unreliability.

