import java.util.LinkedList;
import java.util.List;

class Node {

    protected String ipAddress;
    protected int port;

    protected List<BrokerNode> brokers;

    public Node() {
        this.brokers = new LinkedList<>();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() { return port; }

    public void setPort(int port) { this.port = port; }

    public List<BrokerNode> getBrokers() {
        return brokers;
    }

    public void setBrokers(List<BrokerNode> brokers) { this.brokers = brokers; }
}
