import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

class Broker extends Node {

    /*
    this class handles the requests that the broker receives

    if the input object is instance of 'ListOfBrokers' sends back to consumer
    the list of all brokers

    if the input object is instance of 'ArtistName' makes a socket to the appropriate
    publisher to send it the object

    if the input object is instance of 'MusicFile' it sends it back to the appropriate consumer
     */
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

                if (object instanceof ListOfBrokers) {
                    ((ListOfBrokers) object).setListOfBrokers(brokers);
                    out.writeObject(object);
                } else if (object instanceof ArtistName) {
                    System.out.println(object);

                    new Thread() {
                        @Override
                        public void run() {
                            Socket publisherSocket = null;
                            ObjectOutputStream publisherOut = null;
                            ObjectInputStream publisherIn = null;

                            try {
                                publisherSocket = new Socket("127.0.0.1", 4321);
                                publisherOut = new ObjectOutputStream(publisherSocket.getOutputStream());
                                publisherIn = new ObjectInputStream(publisherSocket.getInputStream());

                                publisherOut.writeObject(object);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }

                            while (true) {
                                try {
                                    Object musicFile = publisherIn.readObject();
                                    System.out.println(musicFile);
                                    out.writeObject(musicFile);
                                    break;
                                } catch (UnknownHostException unknownHost) {
                                    System.err.println("You are trying to connect to an unknown host!");
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
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

    public void setPublishersList(List<PublisherNode> publishersList) {
        this.publishersList = publishersList;
    }

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
        publisherNodes.add(new PublisherNode("127.0.0.1",4321));

        List<BrokerNode> brokerNodes = new LinkedList<>();

        BrokerNode brokerNode1 = new BrokerNode("127.0.0.1",5432);

        brokerNodes.add(brokerNode1);

        Broker broker1 = new Broker("127.0.0.1",5432);
        broker1.setBrokers(brokerNodes);
        broker1.setPublishersList(publisherNodes);

        broker1.listen();
    }
}
