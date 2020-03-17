import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

class ArtistName implements Serializable {

    private String artistName;

    public ArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistName() {
        return artistName;
    }

    @Override
    public boolean equals(Object obj) {
        return this.artistName.equals(((ArtistName) obj).artistName)? true: false;
    }

    @Override
    public int hashCode() {
        return Math.abs(artistName.hashCode());
    }

    @Override
    public String toString() {return this.artistName;}
}
