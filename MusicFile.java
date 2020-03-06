
public class MusicFile {
	
	String trackName;
	String artistName;
	String albumInfo;
	String genre;
	byte[] musicFileExtract;
	
	
	
	/**Music file default constructor*/
	public MusicFile(String trackName, String artistName, String albumInfo, String genre, byte[] musicFileExtract) {
		this.trackName = trackName;
		this.artistName = artistName;
		this.albumInfo = albumInfo;
		this.genre = genre;
		this.musicFileExtract = musicFileExtract;
	}
	
	/**Returns track name*/
	public String getTrackName() {
		return trackName;
	}
	/**Sets track name to trackName*/
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}
	/**Returns artist name*/
	public String getArtistName() {
		return artistName;
	}
	/**Sets artist name to artistName*/
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	/**Returns album info*/
	public String getAlbumInfo() {
		return albumInfo;
	}
	/**Sets album info to albumInfo*/
	public void setAlbumInfo(String albumInfo) {
		this.albumInfo = albumInfo;
	}
	/**Returns genre*/
	public String getGenre() {
		return genre;
	}
	/**Sets genre to genre*/
	public void setGenre(String genre) {
		this.genre = genre;
	}
	/**Returns music file as a byte array*/
	public byte[] getMusicFileExtract() {
		return musicFileExtract;
	}
	/**Sets music file to a byte array*/
	public void setMusicFileExtract(byte[] musicFileExtract) {
		this.musicFileExtract = musicFileExtract;
	}

}
