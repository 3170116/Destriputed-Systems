
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getByName("192.168.1.11");
        
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
   
    
        //establish socket connection to server
        socket = new Socket(host.getHostName(), 5501);
        //write to socket using ObjectOutputStream
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending request to Socket Server");
        oos.writeObject("she");
        
        //read the server response message
        ois = new ObjectInputStream(socket.getInputStream());
        
        int mfSize =  ois.readInt(); //File size to be recieve
       	System.out.println("Server says file is " + mfSize + " Bytes!");
       	MusicFile mFile =  new MusicFile(mfSize);
      	
       	int n = 6400;int TotalChunks = mfSize/n;

        for(int i=0;i<TotalChunks;i++){
        	ois.readFully(mFile.musicFileExtract,n*i,n);	
        }
        System.out.println(mfSize-TotalChunks*n + " REMAIN");
        ois.readFully(mFile.musicFileExtract ,TotalChunks*n ,mfSize-TotalChunks*n);

        System.out.println("I got everything!.Thanks server! <3");
        MusicFile.saveMusicFile(mFile,"copia");
        
        //close resources
        ois.close();
        oos.close();
        socket.close();

    }
}
