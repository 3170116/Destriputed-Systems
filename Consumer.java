import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

class Consumer extends Node {

    private int maxBrokerHashKey;

    public Consumer() {
        maxBrokerHashKey = 0;
    }

    public int hashKey(BrokerNode broker) {
        return Math.abs((broker.getIpAddress() + broker.getPort()).hashCode());
    }

    //finds the broker which can send the song of artist 'artistName'
    public void push(TrackName trackName) {
        new Thread() {
            @Override
            public void run() {
                Queue<Object> chunks = new LinkedList<>();
                int totalBytes = 0;//the sum of bytes which the MusicFile objects will have

                Socket requestSocket = null;
                ObjectOutputStream out = null;
                ObjectInputStream in = null;

                for (BrokerNode broker: brokers) {
                    if (trackName.hashCode()%maxBrokerHashKey <= hashKey(broker)) {
                        //makes a socket connection with the broker
                        try {
                            requestSocket = new Socket(broker.getIpAddress(), broker.getPort());
                            out = new ObjectOutputStream(requestSocket.getOutputStream());
                            in = new ObjectInputStream(requestSocket.getInputStream());

                            out.writeObject(trackName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }

                while (true) {
                    try {
                        MusicFile object = (MusicFile) in.readObject();
                        if (object.getMusicFileExtract() == null) {
                            System.out.println("File not found!");
                            break;
                        } else {
                            System.out.println(object);

                            chunks.add(object);
                            totalBytes +=  object.getMusicFileExtract().length;

                            if (object.isLast()) {
                                if (object.save()) {
                                    //merge all chunks into one file and save it to temporary folder
                                    File tempFile = File.createTempFile(((MusicFile) object).getTrackName(), ".mp3", null);
                                    FileOutputStream fos = new FileOutputStream(tempFile);

                                    System.out.println("Saved to: " + tempFile.getAbsolutePath());

                                    byte[] musicBytes = new byte[totalBytes];

                                    int i = 0;
                                    while (!chunks.isEmpty()) {
                                        for (int j = 0; j < ((MusicFile) chunks.peek()).getMusicFileExtract().length; j++)
                                            musicBytes[i++] = ((MusicFile) chunks.peek()).getMusicFileExtract()[j];
                                        chunks.poll();
                                    }

                                    fos.write(musicBytes);
                                    fos.close();
                                }
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    public void getArtists() {
        ListOfArtists artists;

        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;

        try {
            socket = new Socket("127.0.0.1", 5432);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            objectOutputStream.writeObject(new ListOfArtists());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            artists = (ListOfArtists) objectInputStream.readObject();

            for (String artist: artists.getArtists())
                System.out.println(artist);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                objectInputStream.close();
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void getSongOfArtist(String artist) {
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;

        for (BrokerNode broker: brokers) {
            if (artist.hashCode()%maxBrokerHashKey <= hashKey(broker)) {
                try {
                    socket = new Socket(broker.getIpAddress(), broker.getPort());
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectInputStream = new ObjectInputStream(socket.getInputStream());

                    objectOutputStream.writeObject(new ListOfSongs(artist));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

        try {
            ListOfSongs songs = (ListOfSongs) objectInputStream.readObject();

            for (String song: songs.getSongs())
                System.out.println(song);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                objectInputStream.close();
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //we call register the first time to get the list of all brokers
    public void register() {
        if (!brokers.isEmpty())
            return;

        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            requestSocket = new Socket("127.0.0.1", 5432);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeObject(new ListOfBrokers());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            brokers = ((ListOfBrokers) in.readObject()).getListOfBrokers();

            //calculate the broker with the max hashKey
            int key = 0;
            for (BrokerNode brokerNode : brokers)
                if (hashKey(brokerNode) > key)
                    key = hashKey(brokerNode);

            maxBrokerHashKey = key;

            //sort the brokers according to hashCode
            Collections.sort(brokers);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                requestSocket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Consumer consumer = new Consumer();

        consumer.register();

        consumer.getArtists();

        consumer.getSongOfArtist("Alexander Nakarada");

        TrackName trackName = new TrackName("Hor Hor","Alexander Nakarada");
        trackName.save(false);
        consumer.push(trackName);
    }

}
