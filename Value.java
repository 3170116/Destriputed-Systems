import java.io.IOException;
import java.io.Serializable;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class Value implements Serializable{
	
	MusicFile musicFile;
	boolean hasNext;
	
	/**Default Constructor*/
	public Value(MusicFile musicFile, boolean hasNext) {
		super();
		this.musicFile = musicFile;
		this.hasNext = hasNext;
	}



	public MusicFile getMusicFile() {
		return musicFile;
	}

	public void setMusicFile(MusicFile musicFile) {
		this.musicFile = musicFile;
	}
	
	
	
	
}
