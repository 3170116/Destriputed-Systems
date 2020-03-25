import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class MusicFile implements Serializable {
	
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

	public MusicFile(int mfSize){
		this.musicFileExtract = new byte[mfSize];
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
	
	
	
	public static List<Value> readMusicFile(String name)   throws IOException {
		
		String track = "Unknown";
		String artist = "Unknown";
		String title = "Unknown";
		String album = "Unknown";
		String year = "Unknown";
		String genre = "Unknown";
		
		Mp3File mp3file = null;
		try {
			mp3file = new Mp3File("C:\\Users\\HP\\Desktop\\HW1_DIST\\she.mp3");
		} catch (UnsupportedTagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mp3file.hasId3v2Tag()) {
			ID3v2 id3v2Tag = mp3file.getId3v2Tag();
		  
			track = id3v2Tag.getTrack();
			artist = id3v2Tag.getArtist();
			title = id3v2Tag.getTitle();
			album = id3v2Tag.getAlbum();
			year = id3v2Tag.getYear();
			genre = id3v2Tag.getGenreDescription();
		}
		else if (mp3file.hasId3v1Tag()) {
			ID3v1 id3v1Tag = mp3file.getId3v1Tag();
			track = id3v1Tag.getTrack();
			artist = id3v1Tag.getArtist();
			title = id3v1Tag.getTitle();
			album = id3v1Tag.getAlbum();
			year = id3v1Tag.getYear();
			genre = id3v1Tag.getGenreDescription();
		}
		List<Value> chunks = new ArrayList<Value>();
		
		byte [] musicFileExtract = Files.readAllBytes(Paths.get("C:\\Users\\HP\\Desktop\\HW1_DIST\\" +name + ".mp3"));
		
		 //Chunk size n and total chunks to be sent
        int n = 524288;int mfSize = musicFileExtract.length;int TotalChunks = mfSize/n;
        for(int i=0;i<TotalChunks;i++){
        	chunks.add(new Value( 
        			new MusicFile(title,artist, album,genre, Arrays.copyOfRange(musicFileExtract,n*i,n*(i+1)))
        			, true));
      
        }
		//Last chunk
    	chunks.add(new Value( 
    			new MusicFile(title,artist, album,genre, Arrays.copyOfRange(musicFileExtract,TotalChunks*n,mfSize)), false));
  
		
		return chunks;
	}
	
	public static void saveMusicFile(MusicFile musicfile, String name) throws IOException {
		
		try (FileOutputStream stream = new FileOutputStream(name + ".mp3")) {
		    stream.write(musicfile.musicFileExtract);
		}
		
	}
	

}
