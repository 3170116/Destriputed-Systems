import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

class ArtistName implements Serializable {

    private String artistName;

    private String consumerIp;

    public ArtistName(String artistName, String consumerIp) {
        this.artistName = artistName;
        this.consumerIp = consumerIp;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getConsumerIp() {
        return consumerIp;
    }

    public void setConsumerIp(String consumerIp) {
        this.consumerIp = consumerIp;
    }

    @Override
    public boolean equals(Object obj) {
        return this.artistName.equals(((ArtistName) obj).artistName)? true: false;
    }

    @Override
    public String toString() {return this.artistName;}
}
