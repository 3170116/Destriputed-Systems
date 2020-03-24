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

	public MusicFile(int mfSize){ this.musicFileExtract = new byte[mfSize]; }

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

	public static MusicFile readMusicFile(String name)   throws IOException {
		String artistName = name;
		String trackName = null;
		String duration = null;
		String albumInfo = null;
		String genre = null;
		
		byte [] musicFileExtract = Files.readAllBytes(Paths.get("Electronic/" + name + ".mp3"));

		return new MusicFile(trackName,artistName, albumInfo,genre,musicFileExtract);
	}
	
	public static void saveMusicFile(MusicFile musicfile, String name) throws IOException {
		try (FileOutputStream stream = new FileOutputStream("Electronic/" + name + ".mp3")) {
		    stream.write(musicfile.musicFileExtract);
		}
	}

	@Override
	public String toString() {
		return artistName + " bytes: " + musicFileExtract.length;
	}
}
