package edu.Groove9.TunesMaster.songplayer.domain.usecase;

import android.support.annotation.NonNull;

import edu.Groove9.TunesMaster.UseCase;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.songplayer.player.AudioPlayerContract;
import edu.Groove9.TunesMaster.songplayer.player.SongStatus;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Raktima on 2/23/2017.
 */

public class RepeatSong extends UseCase<RepeatSong.RequestValues, RepeatSong.ResponseValue> {
    private final AudioPlayerContract musicPlayer;
    public RepeatSong(@NonNull AudioPlayerContract musicPlayer) {
        this.musicPlayer = checkNotNull(musicPlayer, "musicPlayer cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        Song song = values.getSong();
        musicPlayer.play(song);
        getUseCaseCallback().onSuccess(new RepeatSong.ResponseValue());

    }
    public static final class RequestValues implements UseCase.RequestValues {

        private final Song song;

        public RequestValues(@NonNull Song song) {
            this.song = checkNotNull(song, "song cannot be null!");
        }

        public Song getSong() {
            return song;
        }
    }
    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
