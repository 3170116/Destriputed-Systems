import java.io.Serializable;

class ArtistName implements Serializable {

    private String artistName;
    private boolean save;

    public ArtistName(String artistName) {
        this.artistName = artistName;
        this.save = false;
    }

    public String getArtistName() {
        return artistName;
    }

    public boolean save() { return save; }

    public void save(boolean save) { this.save = save; }

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
