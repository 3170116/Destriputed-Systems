import java.io.Serializable;

/*
we use this class to send to consumers which brokers exist
 */
class BrokerNode implements Serializable, Comparable<BrokerNode> {

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

    @Override
    public int compareTo(BrokerNode o) {
        return Math.abs((ipAddress + port).hashCode()) < Math.abs((o.getIpAddress() + o.getPort()).hashCode())? -1: 1;
    }
}