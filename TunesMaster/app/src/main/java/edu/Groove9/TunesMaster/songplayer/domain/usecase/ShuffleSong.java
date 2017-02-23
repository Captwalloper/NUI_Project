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
import edu.Groove9.TunesMaster.data.source.SongsRepository;
import edu.Groove9.TunesMaster.playlist.domain.model.Playlist;
import edu.Groove9.TunesMaster.songplayer.player.AudioPlayerContract;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Deletes tasks marked as completed.
 */
public class ShuffleSong extends UseCase<ShuffleSong.RequestValues, ShuffleSong.ResponseValue> {

    private final AudioPlayerContract musicPlayer;

    public ShuffleSong(@NonNull AudioPlayerContract musicPlayer) {
        this.musicPlayer = checkNotNull(musicPlayer, "musicPlayer cannot be null!");
    }

    @Override
    protected void executeUseCase(final ShuffleSong.RequestValues values) {
        Playlist playlist = values.getPlaylist();

        playlist.Shuffle();

        getUseCaseCallback().onSuccess(new ShuffleSong.ResponseValue(playlist));
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
