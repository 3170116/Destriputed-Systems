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
		
}
