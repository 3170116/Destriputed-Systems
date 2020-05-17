import java.io.Serializable;
import java.util.List;

public class ListOfSongs implements Serializable {
    private String artist;
    private List<String> songs;

    public ListOfSongs(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public List<String> getSongs() {
        return songs;
    }

    public void setSongs(List<String> songs) {
        this.songs = songs;
    }
}
