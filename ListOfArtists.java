import java.io.Serializable;
import java.util.List;

public class ListOfArtists implements Serializable {
    private List<String> artists;

    public ListOfArtists() {
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
    }
}
