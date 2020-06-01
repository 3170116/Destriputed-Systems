package com.myapps.ds;

class TrackName {

    private String trackName;
    private String artistName;
    private boolean save;

    public TrackName(String trackName, String artistName) {
        this(trackName,artistName,false);
    }

    public TrackName(String trackName, String artistName, boolean save) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.save = save;
    }

    public String getTrackName() { return trackName; }

    public String getArtistName() { return artistName; }

    public boolean save() { return save; }

    public void save(boolean save) { this.save = save; }

    @Override
    public boolean equals(Object obj) { return (this.trackName + this.artistName).equals(((TrackName) obj).trackName + ((TrackName) obj).artistName)? true: false; }

    @Override
    public int hashCode() {
        return Math.abs(artistName.hashCode());
    }

    @Override
    public String toString() {return this.trackName;}
}
