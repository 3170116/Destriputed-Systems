public class PublisherNode {

    private String ipAddress;
    private int port;

    private String fromLetter;
    private String toLetter;

    public PublisherNode(String ipAddress, int port, String fromLetter, String toLetter) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.fromLetter = fromLetter;
        this.toLetter = toLetter;
    }

    public String getIpAddress() { return ipAddress; }

    public int getPort() {
        return port;
    }

    public boolean handlesArtist(String letter) {
        return fromLetter.compareTo(letter) <= 0 && letter.compareTo(toLetter) <= 0;
    }
}