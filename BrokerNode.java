import java.io.Serializable;

/*
we use this class to send to consumers which brokers exist
 */
class BrokerNode implements Serializable {

    private String ipAddress;
    private int port;

    public BrokerNode(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

}