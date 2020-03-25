import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

class Publisher extends Node {

    /*
        handles the connection between the publisher and a broker
     */
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
                Object object = in.readObject();//title
                MusicFile mfile = MusicFile.readMusicFile(((TrackName) object).getTrackName());

                //file found!
                if (mfile.getMusicFileExtract() != null) {
                    System.out.println(mfile);

                    //Chunk size n and total chunks to be sent
                    int n = 1024;
                    int mfSize = mfile.getMusicFileExtract().length;

                    if (n >= mfSize) {
                        mfile.isLast(true);
                        mfile.save(((TrackName) object).save());
                        out.writeObject(mfile);
                    }

                    for (int i = n;; i += n){

                        byte[] tmpBytes = Arrays.copyOfRange(mfile.getMusicFileExtract(), i - n, Math.min(mfSize,i));
                        MusicFile tmpFile = new MusicFile(mfile.getTrackName(),mfile.getArtistName(),mfile.getAlbumInfo(),mfile.getGenre(),tmpBytes);

                        if (mfSize <= i)
                            tmpFile.isLast(true);
                        tmpFile.save(((TrackName) object).save());

                        out.writeObject(tmpFile);

                        if (i >= mfSize)
                            break;
                    }
                } else {
                    out.writeObject(mfile);
                }

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
        //Publisher publisher2 = new Publisher("127.0.0.1",4322);

        publisher1.pull();
        //publisher2.pull();
    }

}
