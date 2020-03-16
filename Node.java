import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

class Node {

    protected String ipAddress;//we use it as ID
    protected List<BrokerNode> brokers;

    public Node() {
        this.brokers = new LinkedList<>();
    }
    public Node(List<BrokerNode> brokers) {
        this.brokers = brokers;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public List<BrokerNode> getBrokers() {
        return brokers;
    }

    public void setBrokers(List<BrokerNode> brokers) { this.brokers = brokers; }
}
