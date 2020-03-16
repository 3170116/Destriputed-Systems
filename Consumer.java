import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

class Consumer extends Node {

    private Thread pushThread;

    private Socket requestSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;


    public Consumer() {
        try {
            requestSocket = new Socket("127.0.0.1", 5432);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        pushThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Object object = in.readObject();
                        //the first time we get all the brokers
                        if (object instanceof ListOfBrokers) {
                            brokers = ((ListOfBrokers) object).getListOfBrokers();
                        } else {
                            System.out.println(object);
                        }
                    } catch (UnknownHostException unknownHost) {
                        System.err.println("You are trying to connect to an unknown host!");
                    } catch (SocketException e) {

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        pushThread.start();
    }

    //finds the broker which can send the song of artist 'artistName'
    public void push(ArtistName artistName) {
        for (BrokerNode broker: brokers)
            if (broker.getArtistNames().contains(artistName)) {
                this.disconnect();

                //makes a socket connection with the broker
                try {
                    requestSocket = new Socket(broker.getIpAddress(),broker.getPort());
                    out = new ObjectOutputStream(requestSocket.getOutputStream());
                    in = new ObjectInputStream(requestSocket.getInputStream());

                    out.writeObject(artistName);
                } catch (IOException e) {
                    e.printStackTrace();
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

    public boolean disconnect() {
        if (in !=  null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            requestSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return false;
        }

        return true;
    }


    public static void main(String[] args) {
        Consumer consumer = new Consumer();

        consumer.register();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        consumer.push(new ArtistName("Oikonomopoulos",consumer.getIpAddress()));
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        consumer.disconnect();
    }

}
