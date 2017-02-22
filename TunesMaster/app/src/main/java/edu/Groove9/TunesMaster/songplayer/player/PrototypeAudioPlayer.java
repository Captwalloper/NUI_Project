package edu.Groove9.TunesMaster.songplayer.player;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

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
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
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

    private boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
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
