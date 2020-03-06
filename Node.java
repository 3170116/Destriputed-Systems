import java.util.List;

public class Node {
	
	List <Broker> brokers;
	
	/**Empty Constructor*/
	public Node() {
		this.brokers = brokers;
	}
	/**Default Constructor*/
	public Node(List<Broker> brokers) {
		this.brokers = brokers;
	}
	
	
	
	public void setBrokers(List<Broker> brokers) {
		this.brokers = brokers;
	}
	
	public void init(int i) {}
	public List<Broker> getBrokers() {return brokers;}
	public void connect() {}
	public void discconect() {}
	public void updateNodes() {}
		
}
