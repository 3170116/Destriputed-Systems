import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

class MusicFile implements Serializable {
	
    private String trackName;
    private String artistName;
    private String albumInfo;
    private String genre;
    private byte[] musicFileExtract;

    private String consumerIp;

    public MusicFile(String trackName, String artistName, String albumInfo, String genre, byte[] musicFileExtract) {
            this.trackName = trackName;
            this.artistName = artistName;
            this.albumInfo = albumInfo;
            this.genre = genre;
            this.musicFileExtract = musicFileExtract;
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

    public String getConsumerIp() {
        return consumerIp;
    }

    public void setConsumerIp(String consumerIp) {
        this.consumerIp = consumerIp;
    }

    @Override
    public String toString() {
        return artistName;
    }
}
