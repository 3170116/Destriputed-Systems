package com.myapps.ds;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.*;

public class Consumer {

    private List<BrokerNode> brokers;
    private int maxBrokerHashKey;

    private Context context;

    public Consumer() {
        maxBrokerHashKey = 0;
        brokers = new LinkedList<>();
    }

    public List<BrokerNode> getBrokers() {
        return brokers;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int hashKey(BrokerNode broker) {
        return Math.abs((broker.getIpAddress() + broker.getPort()).hashCode());
    }

    //finds the broker which can send the song of artist 'artistName'
    public void push(final TrackName trackName) {

        new Thread() {
            @Override
            public void run() {
                Queue<MusicFile> chunks = new LinkedList<>();
                int totalBytes = 0;//the sum of bytes which the MusicFile objects will have

                Socket requestSocket = null;
                ObjectOutputStream out = null;
                ObjectInputStream in = null;

                for (BrokerNode broker: brokers) {
                    if (trackName.hashCode()%maxBrokerHashKey <= hashKey(broker)) {
                        //makes a socket connection with the broker
                        try {
                            requestSocket = new Socket(broker.getIpAddress(), broker.getPort());
                            out = new ObjectOutputStream(requestSocket.getOutputStream());
                            in = new ObjectInputStream(requestSocket.getInputStream());

                            JSONObject json = new JSONObject();
                            json.put("TYPE","TRACKNAME");
                            json.put("ARTIST",trackName.getArtistName());
                            json.put("TRACK",trackName.getTrackName());
                            json.put("SAVE",trackName.save());

                            out.writeObject(json.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }

                while (true) {
                    try {
                        JSONObject result = new JSONObject((String) in.readObject());
                        if (result.get("BYTES") == "") {
                            Log.v("Not file","File not found!");
                            break;
                        } else {
                            String[] bytes = result.get("BYTES").toString().split(",");
                            byte[] chunk = new byte[bytes.length];
                            for (int i = 0; i < bytes.length; i++)
                                chunk[i] = Byte.parseByte(bytes[i]);

                            chunks.add(new MusicFile(result.get("TRACKNAME").toString(),result.get("ARTIST").toString(),result.get("ALBUM").toString(),result.get("GENRE").toString(),chunk));
                            Log.v("Chunk",new MusicFile(result.get("TRACKNAME").toString(),result.get("ARTIST").toString(),result.get("ALBUM").toString(),result.get("GENRE").toString(),chunk).toString());

                            totalBytes +=  chunk.length;

                            if ((boolean) result.get("LAST") == true) {
                                if ((boolean) result.get("SAVE") == true) {

                                    byte[] musicBytes = new byte[totalBytes];

                                    int i = 0;
                                    while (!chunks.isEmpty()) {
                                        for (int j = 0; j < chunks.peek().getMusicFileExtract().length; j++)
                                            musicBytes[i++] = chunks.peek().getMusicFileExtract()[j];
                                        chunks.poll();
                                    }

                                    try{

                                        File songFile =new File(context.getFilesDir(),trackName.getTrackName());
                                        if(!songFile.exists())
                                            songFile.createNewFile();

                                        OutputStream output = new FileOutputStream(songFile);

                                        output.write(musicBytes);

                                        output.flush();
                                        output.close();

                                        ArtistsFragment.activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(ArtistsFragment.activity,"Saved!",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        Log.v("Saved ",context.fileList().length + "");
                                    } catch(Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    byte[] musicBytes = new byte[totalBytes];

                                    int i = 0;
                                    while (!chunks.isEmpty()) {
                                        for (int j = 0; j < chunks.peek().getMusicFileExtract().length; j++)
                                            musicBytes[i++] = chunks.peek().getMusicFileExtract()[j];
                                        chunks.poll();
                                    }

                                    // create temp file that will hold byte array
                                    File tempMp3 = null;
                                    try {
                                        tempMp3 = File.createTempFile("temp", "mp3", MainActivity.cacheDir);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    tempMp3.deleteOnExit();
                                    FileOutputStream tempFos = null;

                                    try {
                                        tempFos = new FileOutputStream(tempMp3);
                                        tempFos.write(musicBytes);
                                        tempFos.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                    // resetting mediaplayer instance to evade problems
                                    DownloadsFragment.mediaPlayer.reset();

                                    // In case you run into issues with threading consider new instance like:
                                    // MediaPlayer mediaPlayer = new MediaPlayer();

                                    // Tried passing path directly, but kept getting
                                    // "Prepare failed.: status=0x1"
                                    // so using file descriptor instead
                                    FileInputStream fis = null;
                                    try {
                                        fis = new FileInputStream(tempMp3);
                                        DownloadsFragment.mediaPlayer.setDataSource(fis.getFD());
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        DownloadsFragment.mediaPlayer.prepare();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    DownloadsFragment.mediaPlayer.start();
                                }
                                break;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    public String[] getArtists() {
        this.register();

        String[] artists = null;

        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE","LISTOFARTISTS");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            socket = new Socket("192.168.1.3", 5432);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            objectOutputStream.writeObject(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject result = new JSONObject((String) objectInputStream.readObject());
            artists = result.get("LISTOFARTISTS").toString().split(",");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                objectInputStream.close();
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return artists;
    }

    public String[] getSongOfArtist(String artist) {
        String[] songs = null;

        Socket socket = null;
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;

        for (BrokerNode broker: brokers) {
            if (artist.hashCode()%maxBrokerHashKey <= hashKey(broker)) {

                try {
                    socket = new Socket(broker.getIpAddress(), broker.getPort());
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectInputStream = new ObjectInputStream(socket.getInputStream());

                    JSONObject json = new JSONObject();
                    json.put("TYPE","LISTOFSONGS");
                    json.put("ARTIST",artist);

                    objectOutputStream.writeObject(json.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

        try {
            JSONObject result = new JSONObject((String) objectInputStream.readObject());
            songs = result.get("LISTOFSONGS").toString().split(",");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                objectInputStream.close();
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return songs;
    }

    //we call register the first time to get the list of all brokers
    public void register() {
        if (!brokers.isEmpty())
            return;

        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        JSONObject json = new JSONObject();
        try {
            json.put("TYPE", "LISTOFBROKERS");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            requestSocket = new Socket("192.168.1.3", 5432);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeObject(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject((String) in.readObject());
            String[] brokerNodes = jsonObject.get("LISTOFBROKERS").toString().split("/");

            for (String brokerNode: brokerNodes) {
                brokers.add(new BrokerNode(brokerNode.split(",")[0],Integer.parseInt(brokerNode.split(",")[1])));
            }

            //calculate the broker with the max hashKey
            int key = 0;
            for (BrokerNode brokerNode : brokers)
                if (hashKey(brokerNode) > key)
                    key = hashKey(brokerNode);

            maxBrokerHashKey = key;

            //sort the brokers according to hashCode
            Collections.sort(brokers);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                requestSocket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
