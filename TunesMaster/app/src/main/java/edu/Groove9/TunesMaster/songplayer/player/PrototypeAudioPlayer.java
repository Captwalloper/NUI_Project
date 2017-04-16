package edu.Groove9.TunesMaster.songplayer.player;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;

import java.io.IOException;

import edu.Groove9.TunesMaster.R;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;

/**
 * Created by ConnorM on 2/21/2017.
 */

public class PrototypeAudioPlayer implements AudioPlayerContract {

    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private Context context;

    private PrototypeAudioPlayer(Context context) {
        this.context = context;
    }

    private static PrototypeAudioPlayer singleton = null;

    public static PrototypeAudioPlayer getInstance(Context context) {
        if (singleton == null) {
            singleton =  new PrototypeAudioPlayer(context);
        }
        return singleton;
    }

    @Override
    public void play(Song song) {
        reset();
        String path = song.getSource().getPath();
        try {
            setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            throw new RuntimeException("Failed to play song:\n" + ioe.toString());
        } catch (IllegalStateException ise) {
            mediaPlayer.release();
            play(song); // retry
        }
        this.currentSong = song;
        mediaPlayer.start();
    }

    //HACK
    private void setDataSource(String path) throws IOException {
        // try the Assets folder
        try {
            AssetFileDescriptor afd = context.getAssets().openFd("music/" + path);
            mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            afd.close();
        } catch (IOException e) {
            // it wasn't in the assets folder
//            throw new RuntimeException(e.toString());
            mediaPlayer.setDataSource(path);
        }
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void resume() {
        mediaPlayer.start();
    }

    @Override
    public void stop() {
        this.currentSong = null;
        shutdown();
    }

    @Override
    public void changeVolume(VolumeIncrement increment) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int direction = -1;
        switch(increment) {
            case UP:
                direction = AudioManager.ADJUST_RAISE;
                break;
            case DOWN:
                direction = AudioManager.ADJUST_LOWER;
                break;
            default:
                throw new RuntimeException("Unknown VolumeIncrement: " + increment);
        }
        int flags = 0; // ignore
        final int multiplier = 5;
        for (int i=0; i<multiplier; i++) {
            am.adjustVolume(direction, flags);
        }
    }

    @Override
    public SongStatus getStatus(Song song) {
        if (song.equals(currentSong)) {
            if (isPlaying()) {
                return SongStatus.PLAYING;
            } else {
                return SongStatus.PAUSED;
            }
        } else {
            return SongStatus.NOT_SELECTED;
        }
    }

    @Override
    public int percentageProgress() {
        try {
            int totalLength = mediaPlayer.getDuration();
            int played = mediaPlayer.getCurrentPosition();
            return (played * 100) / totalLength;
        } catch (NullPointerException npe) {
            return 0;
        } catch (IllegalStateException ise) {
            return 0;
        }
    }

    @Override
    public void setPercentProgress(int percent) {
        int totalLength = mediaPlayer.getDuration();
        int seekPositionMs = (totalLength * percent) / 100;
        mediaPlayer.seekTo(seekPositionMs);
    }

    private boolean isPlaying() {
        try {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch (IllegalStateException ise) {
            return true;
        }
    }

    private void setup() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void shutdown() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void reset() {
        if (isPlaying()) {
            stop();
        }
        if (mediaPlayer != null) {
            shutdown();
        }
        setup();
    }
}
