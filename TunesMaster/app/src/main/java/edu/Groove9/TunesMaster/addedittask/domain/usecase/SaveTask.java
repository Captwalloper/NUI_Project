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

package edu.Groove9.TunesMaster.addedittask.domain.usecase;

import android.support.annotation.NonNull;

import edu.Groove9.TunesMaster.UseCase;
import edu.Groove9.TunesMaster.data.source.SongsRepository;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Updates or creates a new {@link Song} in the {@link SongsRepository}.
 */
public class SaveTask extends UseCase<SaveTask.RequestValues, SaveTask.ResponseValue> {

    private final SongsRepository mSongsRepository;

    public SaveTask(@NonNull SongsRepository songsRepository) {
        mSongsRepository = checkNotNull(songsRepository, "songsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        Song song = values.getTask();
        mSongsRepository.saveSong(song);

        getUseCaseCallback().onSuccess(new ResponseValue(song));
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final Song mSong;

        public RequestValues(@NonNull Song song) {
            mSong = checkNotNull(song, "song cannot be null!");
        }

        public Song getTask() {
            return mSong;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Song mSong;

        public ResponseValue(@NonNull Song song) {
            mSong = checkNotNull(song, "song cannot be null!");
        }

        public Song getTask() {
            return mSong;
        }
    }
}
