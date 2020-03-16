import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

class BrokerNode implements Serializable {

    private String ipAddress;
    private int port;

    private List<ArtistName> artistNames;

    public BrokerNode(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.artistNames = new LinkedList<>();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public List<ArtistName> getArtistNames() {
        return artistNames;
    }

    public void add(ArtistName artistName) { this.artistNames.add(artistName); }
}