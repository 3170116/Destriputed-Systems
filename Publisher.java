import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class Publisher extends Node {

    private class BrokerHandler extends Thread {

        private ObjectInputStream in;
        private ObjectOutputStream out;

        public BrokerHandler(Socket connection) {
            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                Object object = in.readObject();
                System.out.println(object);

                MusicFile musicFile = new MusicFile(null,((ArtistName) object).getArtistName(),null,null,new byte[10]);
                out.writeObject(musicFile);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    private int port;

    private Thread pullThread;
    private ServerSocket socket;
    private Socket connection = null;

    public Publisher(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;

        try {
            socket = new ServerSocket(this.port);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void pull() {
        pullThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        connection = socket.accept();

                        BrokerHandler brokerHandler = new BrokerHandler(connection);
                        brokerHandler.run();
                    }
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
        };
        pullThread.start();
    }

    public static void main(String[] args) {
        Publisher publisher1 = new Publisher("127.0.0.1",4321);
        Publisher publisher2 = new Publisher("127.0.0.1",4322);

        publisher1.pull();
        publisher2.pull();
    }

}
