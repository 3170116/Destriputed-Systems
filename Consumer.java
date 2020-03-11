import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

class Consumer {

    private Thread t;

    private Socket requestSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    public Consumer() {
        t = new Thread() {
            @Override
            public void run() {
                try {

                    requestSocket = new Socket("127.0.0.1", 5432);
                    out = new ObjectOutputStream(requestSocket.getOutputStream());
                    in = new ObjectInputStream(requestSocket.getInputStream());

                } catch (UnknownHostException unknownHost) {
                    System.err.println("You are trying to connect to an unknown host!");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } finally {
                    try {
                        in.close(); out.close();
                        requestSocket.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        };
    }

    public void register(Broker broker, ArtistName artname) {
        t.start();
    }

    public void disconnect(Broker broker, ArtistName artname) {}
    public void playData (ArtistName artname, Value value) {}

    public static void main(String[] args) {
        new Consumer().register(null,null);
    }
}
