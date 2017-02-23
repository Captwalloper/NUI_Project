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

import static com.google.common.base.Preconditions.checkNotNull;

import android.support.annotation.NonNull;

import edu.Groove9.TunesMaster.UseCase;
import edu.Groove9.TunesMaster.data.source.SongsRepository;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.data.source.SongsDataSource;

/**
 * Retrieves a {@link Song} from the {@link SongsRepository}.
 */
public class GetTask extends UseCase<GetTask.RequestValues, GetTask.ResponseValue> {

    private final SongsRepository mSongsRepository;

    public GetTask(@NonNull SongsRepository songsRepository) {
        mSongsRepository = checkNotNull(songsRepository, "songsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(final RequestValues values) {
        mSongsRepository.getSong(values.getTaskId(), new SongsDataSource.GetSongCallback() {
            @Override
            public void onSongLoaded(Song song) {
                if (song != null) {
                    ResponseValue responseValue = new ResponseValue(song);
                    getUseCaseCallback().onSuccess(responseValue);
                } else {
                    getUseCaseCallback().onError();
                }
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static final class RequestValues implements UseCase.RequestValues {

        private final String mTaskId;

        public RequestValues(@NonNull String taskId) {
            mTaskId = checkNotNull(taskId, "taskId cannot be null!");
        }

        public String getTaskId() {
            return mTaskId;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private Song mSong;

        public ResponseValue(@NonNull Song song) {
            mSong = checkNotNull(song, "song cannot be null!");
        }

        public Song getTask() {
            return mSong;
        }
    }
}
