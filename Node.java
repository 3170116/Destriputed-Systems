import java.util.LinkedList;
import java.util.List;

class Node {
	
    private List <Broker> brokers;

    public Node() {
        this.brokers = new LinkedList<>();
    }

    public Node(List<Broker> brokers) {
        this.brokers = brokers;
    }

    public List<Broker> getBrokers() {
        return brokers;
    }
    
    public void setBrokers(List<Broker> brokers) {
            this.brokers = brokers;
    }

    public void init(int i) {}
    public void connect() {}
    public void discconect() {}
    public void updateNodes() {}
		
}
