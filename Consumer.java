import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import org.json.*;

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
                Queue<MusicFile> chunks = new LinkedList<>();
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

                            JSONObject json = new JSONObject();
                            json.put("TYPE","TRACKNAME");
                            json.put("ARTIST",trackName.getArtistName());
                            json.put("TRACK",trackName.getTrackName());
                            json.put("SAVE",trackName.save());

                            out.writeObject(json.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }

                while (true) {
                    try {
                        JSONObject result = new JSONObject((String) in.readObject());
                        if (result.get("BYTES") == "") {
                            System.out.println("File not found!");
                            break;
                        } else {
                            String[] bytes = result.get("BYTES").toString().split(",");
                            byte[] chunk = new byte[bytes.length];
                            for (int i = 0; i < bytes.length; i++)
                                chunk[i] = Byte.parseByte(bytes[i]);

                            chunks.add(new MusicFile(result.get("TRACKNAME").toString(),result.get("ARTIST").toString(),result.get("ALBUM").toString(),result.get("GENRE").toString(),chunk));
                            totalBytes +=  chunk.length;

                            System.out.println(new MusicFile(result.get("TRACKNAME").toString(),result.get("ARTIST").toString(),result.get("ALBUM").toString(),result.get("GENRE").toString(),chunk));

                            if ((boolean) result.get("LAST") == true) {
                                if ((boolean) result.get("SAVE") == true) {
                                    //merge all chunks into one file and save it to temporary folder
                                    File tempFile = File.createTempFile(result.get("TRACKNAME").toString(), ".mp3", null);
                                    FileOutputStream fos = new FileOutputStream(tempFile);

                                    System.out.println("Saved to: " + tempFile.getAbsolutePath());

                                    byte[] musicBytes = new byte[totalBytes];

                                    int i = 0;
                                    while (!chunks.isEmpty()) {
                                        for (int j = 0; j < chunks.peek().getMusicFileExtract().length; j++)
                                            musicBytes[i++] = chunks.peek().getMusicFileExtract()[j];
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
        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;

        JSONObject json = new JSONObject();
        json.put("TYPE","LISTOFARTISTS");

        try {
            socket = new Socket("127.0.0.1", 5432);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            objectOutputStream.writeObject(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject result = new JSONObject((String) objectInputStream.readObject());
            String[] artists = result.get("LISTOFARTISTS").toString().split(",");

            for (String artist: artists)
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

                    JSONObject json = new JSONObject();
                    json.put("TYPE","LISTOFSONGS");
                    json.put("ARTIST",artist);

                    objectOutputStream.writeObject(json.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

        try {
            JSONObject result = new JSONObject((String) objectInputStream.readObject());
            String[] songs = result.get("LISTOFSONGS").toString().split(",");

            for (String song: songs)
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

        JSONObject json = new JSONObject();
        json.put("TYPE", "LISTOFBROKERS");

        try {
            requestSocket = new Socket("127.0.0.1", 5432);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeObject(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject((String) in.readObject());
            String[] brokerNodes = jsonObject.get("LISTOFBROKERS").toString().split("/");

            for (String brokerNode: brokerNodes) {
                brokers.add(new BrokerNode(brokerNode.split(",")[0],Integer.parseInt(brokerNode.split(",")[1])));
            }

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
