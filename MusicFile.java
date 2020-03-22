import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MusicFile implements java.io.Serializable{
	
	private String trackName;
	private String artistName;
	private String albumInfo;
	private String genre;
	private byte[] musicFileExtract;

	private boolean isLast;
	
	/**Music file default constructor*/
	public MusicFile(String trackName, String artistName, String albumInfo, String genre, byte[] musicFileExtract) {
		this.trackName = trackName;
		this.artistName = artistName;
		this.albumInfo = albumInfo;
		this.genre = genre;
		this.musicFileExtract = musicFileExtract;

		this.isLast = false;
	}

	public MusicFile(int mfSize){ this.musicFileExtract = new byte[mfSize]; }
	
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

	public boolean isLast() { return isLast; }

	public void isLast(boolean isLast) { this.isLast = isLast; }


	public static MusicFile readMusicFile(String name)   throws IOException {
		String artistName = "Mods";
		String trackName = name;
		String duration = "6:15";
		String albumInfo = "Greatest Hits";
		String genre = "Psychedelic Pogressive Techno-Rock";
		
		byte [] musicFileExtract = Files.readAllBytes(Paths.get("out/" + name + ".wav"));

		return new MusicFile(trackName,artistName, albumInfo,genre,musicFileExtract);
	}
	
	public static void saveMusicFile(MusicFile musicfile, String name) throws IOException {
		try (FileOutputStream stream = new FileOutputStream("out/" + name + ".wav")) {
		    stream.write(musicfile.musicFileExtract);
		}
	}

	@Override
	public String toString() {
		return artistName + " bytes: " + musicFileExtract.length;
	}
}
