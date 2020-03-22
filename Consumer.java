import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

class Consumer extends Node {

    private Thread pushThread;
    private boolean logOut = false;

    private Socket requestSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    private int maxBrokerHashKey = 0;

    public Consumer() {
        try {
            /*
            the port 5432 is for the first (default) broker
            so it will receives us the list of all brokers
            */
            requestSocket = new Socket("127.0.0.1", 5432);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        we accept the music files from brokers
         */
        pushThread = new Thread() {
            @Override
            public void run() {
                while (!logOut) {
                    try {
                        Object object = in.readObject();
                        //the first time we get all the brokers
                        if (object instanceof ListOfBrokers) {
                            brokers = ((ListOfBrokers) object).getListOfBrokers();

                            //calculate the broker with the max hashKey
                            int key = 0;
                            for (BrokerNode brokerNode: brokers)
                                if (hashKey(brokerNode) > key)
                                    key = hashKey(brokerNode);

                            maxBrokerHashKey = key;
                        } else {
                            System.out.println(object);

                            /*
                            we create a temporary file at temp folder
                            we store the bytes of music file there
                            then the audio player reads them from it
                            and finally we delete the file

                            File tempFile = File.createTempFile(((MusicFile) object).getArtistName(), ".wav", null);
                            FileOutputStream fos = new FileOutputStream(tempFile);
                            fos.write(((MusicFile) object).getMusicFileExtract());
                            fos.close();

                            SimpleAudioPlayer simpleAudioPlayer = new SimpleAudioPlayer();
                            simpleAudioPlayer.path = tempFile.getAbsolutePath();
                            System.out.println(simpleAudioPlayer.getPath());
                            simpleAudioPlayer.startSong();

                            tempFile.delete();
                            */

                            if (((MusicFile) object).isLast())
                                disconnect();
                            //break;
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

    public void logOut() {this.logOut = true;}

    public static void main(String[] args) {
        Consumer consumer = new Consumer();

        consumer.register();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        consumer.push(new ArtistName("ahem_x"));

        try {
            TimeUnit.SECONDS.sleep(8);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consumer.logOut();
    }

}
