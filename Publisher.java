import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

class Publisher {

    private class BrokerHandler implements Runnable {

        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public BrokerHandler(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
            this.socket = socket;
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println((String) in.readObject());
                    out.writeObject(new String("Hello Broker from Publisher"));
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
    }

    private String ipAddress;
    private int port;

    private List<BrokerHandler> brokersList;
    private ServerSocket brokersSocket;
    private Socket brokersConnection = null;

    public Publisher() {
        this.ipAddress = "127.0.0.1";
        this.port = 4321;

        try {
            brokersSocket = new ServerSocket(this.port);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        brokersList = new LinkedList<>();
    }

    public void listen() {
        while (true) {
            try {
                brokersConnection = brokersSocket.accept();

                InputStream inputStream = brokersConnection.getInputStream();
                OutputStream outputStream = brokersConnection.getOutputStream();

                BrokerHandler consumerHandler = new BrokerHandler(brokersConnection,new ObjectInputStream(inputStream),new ObjectOutputStream(outputStream));
                brokersList.add(consumerHandler);

                Thread thread = new Thread(consumerHandler);
                thread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean disconnect(BrokerHandler brokerHandler) {
        for (BrokerHandler handler: this.brokersList)
            if (handler == brokerHandler) {
                this.brokersList.remove(handler);
                return true;
            }
        return false;
    }

    public static void main(String[] args) {
        Publisher publisher = new Publisher();
        publisher.listen();
    }

}
