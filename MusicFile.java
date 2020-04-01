import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

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

	public static MusicFile readMusicFile(String track, String artist)   throws IOException {
		String artistName = null;
		String albumInfo = null;
		String genre = null;

        Mp3File mp3file = null;
        try {
            mp3file = new Mp3File("dataset/" + track + ".mp3");
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
			return new MusicFile(track,artistName, albumInfo,genre,null);
		}

        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();

            artistName = id3v2Tag.getArtist();
            albumInfo = id3v2Tag.getAlbum();
            genre = id3v2Tag.getGenreDescription();
        }
        else if (mp3file.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();

            artistName = id3v1Tag.getArtist();
            albumInfo = id3v1Tag.getAlbum();
            genre = id3v1Tag.getGenreDescription();
        }

        /* artist is not the appropriate */
		if (!artistName.equals(artist))
			return new MusicFile(track,artistName, albumInfo,genre,null);
		
		byte [] musicFileExtract = Files.readAllBytes(Paths.get("dataset/" + track + ".mp3"));

		return new MusicFile(track,artistName, albumInfo,genre,musicFileExtract);
	}

	@Override
	public String toString() {
		return "Track: " + trackName + " Artist: " + artistName + " Album: " + albumInfo + " Genre: " +  genre + " bytes: " + musicFileExtract.length;
	}
}
