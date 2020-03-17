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

    private int port;

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

        BrokerNode brokerNode2 = new BrokerNode("129.0.0.1",5433);

        brokerNodes.add(brokerNode1);
        //brokerNodes.add(brokerNode2);

        Broker broker1 = new Broker("127.0.0.1",5432);
        broker1.setBrokers(brokerNodes);
        broker1.setPublishersList(publisherNodes);

        //Broker broker2 = new Broker("127.0.0.1",5433);
        //broker2.setBrokers(brokerNodes);
        //broker2.setPublishersList(publisherNodes);

        broker1.listen();
        //broker2.listen();
    }
}
