/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.Groove9.TunesMaster.songplayer.domain.usecase;

import android.support.annotation.NonNull;

import edu.Groove9.TunesMaster.UseCase;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.songplayer.player.AudioPlayerContract;
import edu.Groove9.TunesMaster.songplayer.player.SongStatus;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Marks a task as completed.
 */
public class PlayPauseSong extends UseCase<PlayPauseSong.RequestValues, PlayPauseSong.ResponseValue> {

    private final AudioPlayerContract musicPlayer;

    public PlayPauseSong(@NonNull AudioPlayerContract musicPlayer) {
        this.musicPlayer = checkNotNull(musicPlayer, "musicPlayer cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        Song song = values.getSong();

        SongStatus songStatus = musicPlayer.getStatus(song);
        switch(songStatus) {
            case NOT_SELECTED:
                musicPlayer.play(song);
                break;
            case PAUSED:
                musicPlayer.resume();
                break;
            case PLAYING:
                musicPlayer.pause();
                break;
            default:
                throw new RuntimeException("Unknown case: " + songStatus);
        }

        getUseCaseCallback().onSuccess(new ResponseValue());
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
