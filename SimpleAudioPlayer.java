
import java.io.File;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

@SuppressWarnings("restriction")

public class SimpleAudioPlayer extends Application {
    public static String path;
    private static MediaPlayer player;

    public SimpleAudioPlayer() { }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Media pick = new Media(new File(path).toURI().toString());
        player = new MediaPlayer(pick);

        // Add a mediaView, to display the media. Its necessary !
        // This mediaView is added to a Pane
        MediaView mediaView = new MediaView(player);

        // Add to scene
        Group root = new Group(mediaView);

        // Show the stage
        primaryStage.setTitle("Media Player");
        primaryStage.show();

        // Play the media once the stage is shown

        player.play();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void startSong() {
        launch();
    }
}