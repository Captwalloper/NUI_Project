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

import java.util.List;

import edu.Groove9.TunesMaster.UseCase;
import edu.Groove9.TunesMaster.data.source.SongsRepository;
import edu.Groove9.TunesMaster.data.source.SongsDataSource;
import edu.Groove9.TunesMaster.playlist.PlaylistFilterType;
import edu.Groove9.TunesMaster.playlist.domain.filter.FilterFactory;
import edu.Groove9.TunesMaster.playlist.domain.filter.TaskFilter;
import edu.Groove9.TunesMaster.playlist.domain.model.Playlist;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.songplayer.player.AudioPlayerContract;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Fetches the list of tasks.
 */
public class LastSong extends UseCase<LastSong.RequestValues, LastSong.ResponseValue> {

    private final AudioPlayerContract musicPlayer;

    public LastSong(@NonNull AudioPlayerContract musicPlayer) {
        this.musicPlayer = checkNotNull(musicPlayer, "musicPlayer cannot be null!");
    }

    @Override
    protected void executeUseCase(final LastSong.RequestValues values) {
        Playlist playlist = values.getPlaylist();

        playlist.getLastSong();

        getUseCaseCallback().onSuccess(new LastSong.ResponseValue(playlist));
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final Playlist playlist;

        public RequestValues(@NonNull Playlist playlist) {
            this.playlist = checkNotNull(playlist, "playlist cannot be null!");
        }

        public Playlist getPlaylist() {
            return playlist;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Playlist playlist;

        public ResponseValue(@NonNull Playlist playlist) {
            this.playlist = checkNotNull(playlist, "playlist cannot be null!");
        }

        public Playlist getPlaylist() {
            return playlist;
        }
    }
}
