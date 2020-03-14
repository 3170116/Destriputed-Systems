import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

class Consumer {

    private Thread pullThread;//the thread that receives the song

    private Socket requestSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    public Consumer() {
        try {
            requestSocket = new Socket("127.0.0.1", 5432);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        pullThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println((String) in.readObject());
                    } catch (UnknownHostException unknownHost) {
                        System.err.println("You are trying to connect to an unknown host!");
                    } catch (SocketException e) {
                        try {
                            in.close();
                            out.close();
                            requestSocket.close();
                            break;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
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
            }
        };
        pullThread.start();
    }

    public void register() {
        try {
            out.writeObject(new String("Hello Broker from Consumer"));
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
            }
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            requestSocket.close();
            return true;
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        consumer.register();
        consumer.register();
        consumer.register();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consumer.disconnect();
    }

}
