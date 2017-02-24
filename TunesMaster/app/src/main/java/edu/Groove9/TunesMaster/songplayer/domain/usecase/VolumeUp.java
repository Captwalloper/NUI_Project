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
import edu.Groove9.TunesMaster.playlist.domain.model.Playlist;
import edu.Groove9.TunesMaster.songplayer.player.AudioPlayerContract;
import edu.Groove9.TunesMaster.songplayer.player.VolumeIncrement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Marks a task as active (not completed yet).
 */
public class VolumeUp extends UseCase<VolumeUp.RequestValues, VolumeUp.ResponseValue> {

    private final AudioPlayerContract musicPlayer;

    public VolumeUp(@NonNull AudioPlayerContract musicPlayer) {
        this.musicPlayer = checkNotNull(musicPlayer, "musicPlayer cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        musicPlayer.changeVolume(VolumeIncrement.UP);
        getUseCaseCallback().onSuccess(new ResponseValue());
    }

    public static final class RequestValues implements UseCase.RequestValues {
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
    }
}
