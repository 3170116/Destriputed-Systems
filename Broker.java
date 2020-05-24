import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

class Broker extends Node {
    
    private class ConnectionHandler extends Thread {

        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ConnectionHandler(Socket connection) {
            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                Object object = in.readObject();
                JSONObject jsonObject = new JSONObject((String) object);
                JSONObject result = new JSONObject();

                System.out.println(jsonObject.toString());

                if (jsonObject.get("TYPE").equals("LISTOFBROKERS")) {
                    String listOfBrokers = "";
                    for (int i = 0; i < brokers.size(); i++) {
                        if (i == brokers.size() -1)
                            listOfBrokers += brokers.get(i).getIpAddress() + "," + brokers.get(i).getPort();
                        else
                            listOfBrokers += brokers.get(i).getIpAddress() + "," + brokers.get(i).getPort() + "/";
                    }

                    result.put("LISTOFBROKERS",listOfBrokers);

                    out.writeObject(result.toString());
                } else if (jsonObject.get("TYPE").equals("LISTOFARTISTS")) {
                    Socket publisherSocket = new Socket("127.0.0.1", 4321);
                    ObjectOutputStream publisherOut = new ObjectOutputStream(publisherSocket.getOutputStream());
                    ObjectInputStream publisherIn = new ObjectInputStream(publisherSocket.getInputStream());

                    publisherOut.writeObject(new ListOfArtists());

                    try {
                        ListOfArtists artists = (ListOfArtists) publisherIn.readObject();

                        String listOfArtists = "";
                        for (int i = 0; i < artists.getArtists().size(); i++) {
                            if (i == artists.getArtists().size() -1)
                                listOfArtists += artists.getArtists().get(i);
                            else
                                listOfArtists += artists.getArtists().get(i) + ",";
                        }

                        result.put("LISTOFARTISTS",listOfArtists);
                        out.writeObject(result.toString());

                    } catch (UnknownHostException unknownHost) {
                        System.err.println("You are trying to connect to an unknown host!");
                    } catch (EOFException e) {

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    try {
                        publisherIn.close();
                        publisherOut.close();
                        publisherSocket.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } else if(jsonObject.get("TYPE").equals("LISTOFSONGS")) {
                    Socket publisherSocket = null;
                    ObjectOutputStream publisherOut = null;
                    ObjectInputStream publisherIn = null;

                    //find the appropriate publisher
                    String artist = jsonObject.get("ARTIST").toString();
                    for (PublisherNode publisherNode: publishersList) {
                        if (publisherNode.handlesArtist(artist.substring(0,1))) {
                            //makes a socket connection with the broker
                            try {
                                publisherSocket = new Socket(publisherNode.getIpAddress(), publisherNode.getPort());
                                publisherOut = new ObjectOutputStream(publisherSocket.getOutputStream());
                                publisherIn = new ObjectInputStream(publisherSocket.getInputStream());

                                publisherOut.writeObject(new ListOfSongs(artist));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }

                    try {
                        ListOfSongs songs = (ListOfSongs) publisherIn.readObject();

                        String listOfSongs = "";
                        for (int i = 0; i < songs.getSongs().size(); i++) {
                            if (i == songs.getSongs().size() -1)
                                listOfSongs += songs.getSongs().get(i);
                            else
                                listOfSongs += songs.getSongs().get(i) + ",";
                        }

                        result.put("LISTOFSONGS",listOfSongs);
                        out.writeObject(result.toString());

                    } catch (UnknownHostException unknownHost) {
                        System.err.println("You are trying to connect to an unknown host!");
                    } catch (EOFException e) {

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    try {
                        publisherIn.close();
                        publisherOut.close();
                        publisherSocket.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } else if(jsonObject.get("TYPE").equals("TRACKNAME")) {
                    new Thread() {
                        @Override
                        public void run() {
                            Socket publisherSocket = null;
                            ObjectOutputStream publisherOut = null;
                            ObjectInputStream publisherIn = null;

                            //find the appropriate publisher
                            String artist = jsonObject.get("ARTIST").toString();
                            for (PublisherNode publisherNode: publishersList) {
                                if (publisherNode.handlesArtist(artist.substring(0,1))) {
                                    //makes a socket connection with the broker
                                    try {
                                        publisherSocket = new Socket(publisherNode.getIpAddress(), publisherNode.getPort());
                                        publisherOut = new ObjectOutputStream(publisherSocket.getOutputStream());
                                        publisherIn = new ObjectInputStream(publisherSocket.getInputStream());

                                        publisherOut.writeObject(new TrackName(jsonObject.get("TRACK").toString(),jsonObject.get("ARTIST").toString(),(boolean) jsonObject.get("SAVE")));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }

                            while (true) {
                                try {
                                    MusicFile musicFile = (MusicFile) publisherIn.readObject();
                                    JSONObject result = new JSONObject();

                                    String bytes = "";
                                    for (int i = 0; i < musicFile.getMusicFileExtract().length; i++) {
                                        if (i == musicFile.getMusicFileExtract().length -1)
                                            bytes += musicFile.getMusicFileExtract()[i];
                                        else
                                            bytes += musicFile.getMusicFileExtract()[i] + ",";
                                    }

                                    result.put("ARTIST",musicFile.getArtistName());
                                    result.put("TRACKNAME",musicFile.getTrackName());
                                    result.put("ALBUM",musicFile.getAlbumInfo() != null? musicFile.getAlbumInfo(): "");
                                    result.put("GENRE",musicFile.getGenre() != null? musicFile.getGenre(): "");
                                    result.put("BYTES",bytes);
                                    result.put("SAVE",musicFile.save());
                                    result.put("LAST",musicFile.isLast());
                                    out.writeObject(result.toString());

                                    if (musicFile.isLast())
                                        break;
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                    break;
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                publisherIn.close();
                                publisherOut.close();
                                publisherSocket.close();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    }.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private ServerSocket socket;
    private Socket connection = null;

    private List<PublisherNode> publishersList;

    public Broker(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;

        try {
            socket = new ServerSocket(this.port);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void setPublishersList(List<PublisherNode> publishersList) { this.publishersList = publishersList; }

    public void listen() {
        new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        //receives a new object
                        connection = socket.accept();

                        ConnectionHandler consumerHandler = new ConnectionHandler(connection);
                        consumerHandler.run();
                    }
                } catch (SocketException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public static void main(String[] args) {
        List<PublisherNode> publisherNodes = new LinkedList<>();
        publisherNodes.add(new PublisherNode("127.0.0.1",4321,"A","Z"));
        //publisherNodes.add(new PublisherNode("127.0.0.1",4322,"I","Z"));

        List<BrokerNode> brokerNodes = new LinkedList<>();

        BrokerNode brokerNode1 = new BrokerNode("192.168.1.3",5432);
        //BrokerNode brokerNode2 = new BrokerNode("127.0.0.1",5433);

        brokerNodes.add(brokerNode1);
        //brokerNodes.add(brokerNode2);


        Broker broker1 = new Broker("127.0.0.1",5432);
        broker1.setBrokers(brokerNodes);
        broker1.setPublishersList(publisherNodes);
        broker1.listen();

        /*
        Broker broker2 = new Broker("127.0.0.1",5433);
        broker2.setBrokers(brokerNodes);
        broker2.setPublishersList(publisherNodes);
        broker2.listen();
         */
    }
}
