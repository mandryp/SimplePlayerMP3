package simpleplayermp3;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class SimplePlayerMP3 {

    private static PlayListMP3 plmp3;

    private Player player;

    public static void main(String[] args) {

        plmp3 = new PlayListMP3();

    }
    private TrackMP3 prevTrack;

    public void play() {

        if (player != null) stop();
        if (plmp3.getCurTrack().equals(prevTrack)) {
            prevTrack = null;
            return;
        };

        try {

            prevTrack = plmp3.getCurTrack();
            File file = new File(plmp3.getCurTrack().getFileName());
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
            new Thread() {

                @Override
                public void run() {
                    try {
                        player.play();
                    } catch (JavaLayerException ex) {
                        Logger.getLogger(TrackMP3.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }.start();

        } catch (JavaLayerException | FileNotFoundException ex) {
            Logger.getLogger(TrackMP3.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void stop() {

        if (player != null) {
            player.close();
            player = null;
        }
    }

}
