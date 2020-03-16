import java.io.Serializable;
import java.util.List;

class ListOfBrokers implements Serializable {

    private List<BrokerNode> listOfBrokers;

    public ListOfBrokers() { }

    public List<BrokerNode> getListOfBrokers() {
        return listOfBrokers;
    }

    public void setListOfBrokers(List<BrokerNode> listOfBrokers) {
        this.listOfBrokers = listOfBrokers;
    }
}
