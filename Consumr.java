import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Consumr {
	
	static Object brokerList;
	static List<List<Value>> trackList = new ArrayList<List<Value>>();
	static int maxBrokerHashKey;
	
	
	public static void main(String args[]) throws ClassNotFoundException, IOException {
		
		System.out.println("Requesting broker list from :127.0.0.1 - 5432");
		//brokerList = getBrokerList();
		System.out.println("Broker list updated");
		
		//scanner object to read the command line input by user
		Scanner sn = new Scanner(System.in);
		//loop the utility in loop until the user makes the choice to exit
		while(true){
			System.out.println("Search for title:");
		
			String fileRequest;
			//Capture the user input in scanner object and store it in a pre decalred variable
			fileRequest = sn.next();if(fileRequest.equals("0"))System.exit(1);
			
			/*
				TODO pare ton katallhlo broker
			*/
			
			//Request song from broker
			String brokerIP = "127.0.0.1";
			int brokerPort = 5000;
		    Socket socket = null;
		    ObjectOutputStream oos = null;
		    ObjectInputStream ois = null;
		    initSocket(socket,ois,oos,brokerIP,brokerPort);
		    
		    boolean readyToRecieve = true;
		    Object object = ois.readObject();
		    if (object instanceof String) {
		    	System.out.println("All good , i will now recieve the chunks from this broker");
	            
				// create a new thread object 
	            Thread t = new Consume(fileRequest,brokerIP,brokerPort,trackList); 
	            // Invoking the start() method 
	            t.start(); 
	            try { //TODO REMOVE KAPOIA STGMH
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	            System.out.println("I recieved :" + trackList.get(0).get(0).musicFile.albumInfo);
		    
		    }else if (object instanceof List<?>) {
		    	System.out.println("Broker responded with a list of brokers");
		    	/*
		    	 	TODO UPDATE TO BROKER LIST

                    brokers = ((ListOfBrokers) object).getListOfBrokers();

                    //calculate the broker with the max hashKey
                    int key = 0;
                    for (BrokerNode brokerNode: brokers)
                        if (hashKey(brokerNode) > key)
                            key = hashKey(brokerNode);

                    maxBrokerHashKey = key;
                   */
		    }
		}
	}
	
	
 // Requests from a specified socket a list of brokers
/*	public static Object getBrokerList() {
		//IO Streams
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		Object brokerList = null;
		try {
           
            the port 5432 is for the first (default) broker
            so it will receives us the list of all brokers
           
        	Socket requestSocket = new Socket("127.0.0.1", 5432);
	      	out = new ObjectOutputStream(requestSocket.getOutputStream());
	        in = new ObjectInputStream(requestSocket.getInputStream());
	        try {
				brokerList = in.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   	
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
		ListOfBrokers brokers = ((ListOfBrokers) brokerList).getListOfBrokers();

        //calculate the broker with the max hashKey
        int key = 0;
        for (BrokerNode brokerNode: brokers)
            if (hashKey(brokerNode) > key)
                key = hashKey(brokerNode);

        maxBrokerHashKey = key;
		
		return brokerList;
	}
	
	
	public int hashKey(BrokerNode broker) {
	        return Math.abs((broker.getIpAddress() + broker.getPort()).hashCode());
	}
	
	
	//finds the broker which can send the song of artist 'artistName'
    public void push(ArtistName artistName) {
        this.disconnect();
        for (BrokerNode broker: brokers) {
            if (artistName.hashCode()%maxBrokerHashKey <= hashKey(broker)) {
                //makes a socket connection with the broker
                try {
                    requestSocket = new Socket(broker.getIpAddress(), broker.getPort());
                    out = new ObjectOutputStream(requestSocket.getOutputStream());
                    in = new ObjectInputStream(requestSocket.getInputStream());

                    out.writeObject(artistName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    //we call register the first time to get the list of all brokers
    public void register() {
        if (!brokers.isEmpty())
            return;

        try {
            out.writeObject(new ListOfBrokers());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
*/
	
	
	
	//Init a socket connection to broker and oos/ois
		static void initSocket (Socket socket, ObjectInputStream ois, ObjectOutputStream oos, String brokerIP, int brokerPort) {
			//Init socket
		    try {
				socket = new Socket(InetAddress.getByName(brokerIP).getHostName(), brokerPort);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    //Init oos
	        try {
				oos = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	      //Init ois
	        try {
				ois = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			// GET MUSIC FILE DATA
			
			try {
	            TimeUnit.SECONDS.sleep(2);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			
		}
	
	
}

 //Starts a new thread and requests a new track
class Consume extends Thread{
	
	String fileRequest;	//File requested by Consumer
    Socket socket = null;
    String brokerIp = null;
    int brokerPort = 0;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;
    List<Value> buffer = new ArrayList<Value>();
	List<List<Value>> trackList;
    
	public Consume(String fileRequest,String brokerIp, int brokerPort, List<List<Value>> trackList) {
        //Init fileRequest
		this.fileRequest = fileRequest;
		this.brokerIp = brokerIp;
		this.brokerPort = brokerPort;
		this.trackList = trackList;
	}
	
	
	
	@Override
	public void run() {
		
		boolean isLast = false;
		//Receive music file stream and fill buffer
		while(isLast == false) {
			Value chunk = null;
			try {
				chunk = (Value) ois.readObject();
				buffer.add(chunk);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			
			if(chunk.hasNext == false)	isLast = true;
			
		}
		
		trackList.add(buffer);
	}
	
}