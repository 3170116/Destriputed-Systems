import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Server {
    
    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 5501;
    
    public static void main(String args[]) throws IOException, ClassNotFoundException{
        //create the socket server object
        server = new ServerSocket(port);
        
        System.out.println("Starting Server...");
        while(true) {  
            Socket s = null; 
            try 
            { 
                // socket object to receive incoming client requests 
                s = server.accept(); 
                  
                System.out.println("A new client is connected : " + s); 
                  
                // obtaining input and out streams 
                ObjectInputStream dis = new ObjectInputStream(s.getInputStream()); 
                ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream()); 
                  
                System.out.println("Assigning new thread for this client"); 
  
                // create a new thread object 
                Thread t = new ClientHandler(s, dis, dos); 
  
                // Invoking the start() method 
                t.start(); 
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
            
      
        }
        
        
    }
}    
 // ClientHandler class 
    class ClientHandler extends Thread  
    { 
        final ObjectInputStream ois; 
        final ObjectOutputStream oos; 
        final Socket socket; 
          
    
        // Constructor 
        public ClientHandler(Socket socket, ObjectInputStream dis, ObjectOutputStream dos)  
        { 
            this.socket = socket; 
            this.ois = dis; 
            this.oos = dos; 
        } 
        
        
        @Override
        public void run()  
        { 
           
            try {
                //convert ObjectInputStream object to String
                String mfTitle = (String) ois.readObject(); //This is clients respose
                System.out.println("Client looking for MusicFile: " + mfTitle);
                //Read music file from disc
                MusicFile mfile = MusicFile.readMusicFile(mfTitle); 

                //Chunk size n and total chunks to be sent
                int n = 6400;int mfSize = mfile.musicFileExtract.length;int TotalChunks = mfSize/n;
                oos.writeInt(mfSize); // Preparing client to recieve n chunks
                
                System.out.println("Breaking file into " + TotalChunks + "Chunks");
                System.out.println("File is: " + mfSize  + " bytes. Sending : " +(TotalChunks*n + mfSize - n*TotalChunks) );
                

                for(int i=0;i<TotalChunks;i++){
                    oos.write(mfile.musicFileExtract,n*i,n);
                    //System.out.println("Sending chunk :" + i);
                }
               
                System.out.println("Sending last : " + (mfSize-TotalChunks*n));
                oos.write(mfile.musicFileExtract,TotalChunks*n,mfSize-TotalChunks*n);
                oos.flush();
                try {
                    // closing resources 
                    this.ois.close(); 
                    this.oos.close(); 
                      
                }catch(IOException e){ 
                    e.printStackTrace(); 
                } 
                
                    
            }catch(IOException | ClassNotFoundException e) {
                e.printStackTrace(); 
            }
        
        }
        
        
        
        
    }
    
    
