import com.mpatric.mp3agic.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class Publisher extends Node {

    /*
        handles the connection between the publisher and a broker
     */
    private class BrokerHandler extends Thread {

        private ObjectInputStream in;
        private ObjectOutputStream out;

        public BrokerHandler(Socket connection) {
            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                Object object = in.readObject();

                if (object instanceof TrackName) {
                    MusicFile mfile = readMusicFile(((TrackName) object).getTrackName(),((TrackName) object).getArtistName());

                    //file found!
                    if (mfile.getMusicFileExtract() != null) {
                        System.out.println(mfile);

                        //Chunk size n and total chunks to be sent
                        int n = 5*1024;
                        int mfSize = mfile.getMusicFileExtract().length;

                        if (n >= mfSize) {
                            mfile.isLast(true);
                            mfile.save(((TrackName) object).save());
                            out.writeObject(mfile);
                        }

                        for (int i = n;; i += n){

                            byte[] tmpBytes = Arrays.copyOfRange(mfile.getMusicFileExtract(), i - n, Math.min(mfSize,i));
                            MusicFile tmpFile = new MusicFile(mfile.getTrackName(),mfile.getArtistName(),mfile.getAlbumInfo(),mfile.getGenre(),tmpBytes);

                            if (mfSize <= i)
                                tmpFile.isLast(true);
                            tmpFile.save(((TrackName) object).save());

                            out.writeObject(tmpFile);

                            if (i >= mfSize)
                                break;
                        }
                    } else {
                        out.writeObject(mfile);
                    }
                } else if (object instanceof ListOfArtists) {
                    ListOfArtists artists = (ListOfArtists) object;
                    artists.setArtists(findArtists());

                    System.out.println(artists.getArtists().size());
                    out.writeObject(artists);
                } else if (object instanceof ListOfSongs) {
                    ListOfSongs songs = (ListOfSongs) object;
                    songs.setSongs(findSongsOfArtist(songs.getArtist()));

                    System.out.println(songs.getSongs().size());
                    out.writeObject(songs);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } /*finally {
                try {
                    in.close();
                    out.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }*/
        }
    }

    private Thread pullThread;
    private ServerSocket socket;
    private Socket connection = null;

    public Publisher(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;

        try {
            socket = new ServerSocket(this.port);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void pull() {
        pullThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        connection = socket.accept();

                        BrokerHandler brokerHandler = new BrokerHandler(connection);
                        brokerHandler.run();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        };
        pullThread.start();
    }

    public MusicFile readMusicFile(String track, String artist)   throws IOException {
        String artistName = null;
        String albumInfo = null;
        String genre = null;

        Mp3File mp3file = null;
        try {
            mp3file = new Mp3File("dataset/" + track);
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

        byte [] musicFileExtract = Files.readAllBytes(Paths.get("dataset/" + track));

        return new MusicFile(track,artistName, albumInfo,genre,musicFileExtract);
    }

    private List<String> findArtists() {
        List<String> result = new LinkedList<>();

        File[] mp3s = new File("dataset/").listFiles();
        for (File mp3: mp3s) {
            try {
                Mp3File mp3file = new Mp3File("dataset/" + mp3.getName());

                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();

                    if (!result.contains(id3v2Tag.getArtist()))
                        result.add(id3v2Tag.getArtist());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedTagException e) {
                e.printStackTrace();
            } catch (InvalidDataException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private List<String> findSongsOfArtist(String artist) {
        List<String> result = new LinkedList<>();

        File[] mp3s = new File("dataset/").listFiles();
        for (File mp3: mp3s) {
            try {
                Mp3File mp3file = new Mp3File("dataset/" + mp3.getName());

                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();

                    if (!result.contains(id3v2Tag.getArtist()) && id3v2Tag.getArtist().equals(artist))
                        result.add(mp3.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedTagException e) {
                e.printStackTrace();
            } catch (InvalidDataException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Publisher publisher1 = new Publisher("127.0.0.1",4321);
        //Publisher publisher2 = new Publisher("127.0.0.1",4322);

        publisher1.pull();
        //publisher2.pull();
    }

}
