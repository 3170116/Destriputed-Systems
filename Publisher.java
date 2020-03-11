import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Publisher implements Comparable<Publisher> {

    private String ipAddress;
    private int port;

    private ServerSocket providerSocket;
    private Socket connection = null;

    public Publisher() {
        this.ipAddress = "127.0.0.1";
        this.port = 4321;

        try {
            providerSocket = new ServerSocket(this.port, 10);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void accept() {
        while (true) {
            try {
                connection = providerSocket.accept();
                System.out.println("Broker " + connection + "connected");
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

    public void getBrokerList() {}
    public Broker hashTopic(ArtistName artname) {return null;}
    public void push(ArtistName artname, Value value) {}
    public void notifyFailure(Broker broker) {}

    public static void main(String[] args) {
        Publisher publisher = new Publisher();
        publisher.accept();
    }

    @Override
    public int compareTo(Publisher p) {
        if (this.ipAddress.equals(p.ipAddress) && this.port == p.port)
            return 0;
        return -1;
    }
}
