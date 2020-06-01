package com.myapps.ds;

public class MusicFile {

    private String trackName;
    private String artistName;
    private String albumInfo;
    private String genre;
    private byte[] musicFileExtract;

    private boolean isLast;
    private boolean save;

    public MusicFile(String trackName, String artistName, String albumInfo, String genre, byte[] musicFileExtract) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.albumInfo = albumInfo;
        this.genre = genre;
        this.musicFileExtract = musicFileExtract;

        this.isLast = false;
        this.save = false;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumInfo() {
        return albumInfo;
    }

    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public byte[] getMusicFileExtract() {
        return musicFileExtract;
    }

    public void setMusicFileExtract(byte[] musicFileExtract) {
        this.musicFileExtract = musicFileExtract;
    }

    public boolean isLast() { return isLast; }

    public void isLast(boolean isLast) { this.isLast = isLast; }

    public boolean save() { return save; }

    public void save(boolean save) { this.save = save; }

    @Override
    public String toString() {
        return "Track: " + trackName + " Artist: " + artistName + " Album: " + albumInfo + " Genre: " +  genre + " bytes: " + musicFileExtract.length;
    }
}
