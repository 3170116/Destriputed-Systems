import java.io.Serializable;

class TrackName implements Serializable {

    private String trackName;
    private boolean save;

    public TrackName(String trackName) {
        this.trackName = trackName;
        this.save = false;
    }

    public String getTrackName() { return trackName; }

    public boolean save() { return save; }

    public void save(boolean save) { this.save = save; }

    @Override
    public boolean equals(Object obj) { return this.trackName.equals(((TrackName) obj).trackName)? true: false; }

    @Override
    public int hashCode() {
        return Math.abs(trackName.hashCode());
    }

    @Override
    public String toString() {return this.trackName;}
}
