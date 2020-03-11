import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

class Broker {

    private String ipAddress;
    private int port;
	
    private List<Consumer> registeredUsers;
    private List<Publisher> registeredPublishers;

    private ServerSocket providerSocket;
    private Socket connection = null;

    public Broker() {
        this.ipAddress = "127.0.0.1";
        this.port = 5432;

        try {
            providerSocket = new ServerSocket(this.port, 10);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public Publisher acceptConection(Publisher publisher) {
        for (Publisher currentPublisher: registeredPublishers)
            if (publisher.compareTo(currentPublisher) == 0)
                return publisher;
        return null;
    }

    public List<Consumer> getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(List<Consumer> registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public List<Publisher> getRegisteredPublishers() {
        return registeredPublishers;
    }

    public void setRegisteredPublishers(List<Publisher> registeredPublishers) {
        this.registeredPublishers = registeredPublishers;
    }

    public void acceptConsumers() {
        while (true) {
            try {
                connection = providerSocket.accept();
                new Socket("127.0.0.1", 4321);
                System.out.println("Consumer " + connection + "connected");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                /*try {
                    providerSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }*/
            }
        }
    }
    
    public void calculateKeys() {}
    public Consumer acceptConnection(Consumer consumer) {return consumer;}
    public void notifyPublisher(String str){}
    public void pull(ArtistName artname) {}

    public static void main(String[] args) {
        Broker broker = new Broker();
        broker.acceptConsumers();
    }
}
