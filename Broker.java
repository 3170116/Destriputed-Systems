import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

class Broker {

    private String ipAddress;
    private int port;

    private List<ConsumerHandler> consumersList;
    private ServerSocket consumersSocket;
    private Socket consumersConnection = null;

    private Socket publisherSocket = null;
    private ObjectOutputStream publisherOut = null;
    private ObjectInputStream publisherIn = null;

    private class ConsumerHandler implements Runnable {

        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ConsumerHandler(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
            this.socket = socket;
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println((String) in.readObject());
                    out.writeObject(new String("Hello Consumer from Broker"));
                    publisherOut.writeObject("Hello Publisher from Broker");
                } catch (UnknownHostException unknownHost) {
                    System.err.println("You are trying to connect to an unknown host!");
                } catch (SocketException e) {
                    try {
                        in.close();
                        out.close();
                        socket.close();
                        break;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } catch (EOFException e) {
                    break;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                this.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            disconnect(this);
        }

        public void send(Object msg) {
            try {
                out.writeObject(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Broker() {
        this.ipAddress = "127.0.0.1";
        this.port = 5432;

        try {
            consumersSocket = new ServerSocket(this.port);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        consumersList = new LinkedList<>();

        try {
            publisherSocket = new Socket("127.0.0.1", 4321);
            publisherOut = new ObjectOutputStream(publisherSocket.getOutputStream());
            publisherIn = new ObjectInputStream(publisherSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String msg = (String) publisherIn.readObject();
                        System.out.println(msg);
                        consumersList.get(0).send(msg);
                    } catch (UnknownHostException unknownHost) {
                        System.err.println("You are trying to connect to an unknown host!");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void listen() {
        while (true) {
            try {
                consumersConnection = consumersSocket.accept();

                InputStream inputStream = consumersConnection.getInputStream();
                OutputStream outputStream = consumersConnection.getOutputStream();

                ConsumerHandler consumerHandler = new ConsumerHandler(consumersConnection,new ObjectInputStream(inputStream),new ObjectOutputStream(outputStream));
                consumersList.add(consumerHandler);

                Thread thread = new Thread(consumerHandler);
                thread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean disconnect(ConsumerHandler consumerHandler) {
        for (ConsumerHandler handler: this.consumersList)
            if (handler == consumerHandler) {
                this.consumersList.remove(handler);
                return true;
            }
        return false;
    }

    public static void main(String[] args) {
        Broker broker = new Broker();
        broker.listen();
    }
}
