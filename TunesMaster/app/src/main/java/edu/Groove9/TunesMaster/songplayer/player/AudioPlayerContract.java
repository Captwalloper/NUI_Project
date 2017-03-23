package edu.Groove9.TunesMaster.songplayer.player;

import edu.Groove9.TunesMaster.playlist.domain.model.Song;

/**
 * Created by ConnorM on 2/21/2017.
 */

public interface AudioPlayerContract {

    void play(Song song);

    void pause();

    void resume();

    void stop();

    void changeVolume(VolumeIncrement increment);

    SongStatus getStatus(Song song);

}
