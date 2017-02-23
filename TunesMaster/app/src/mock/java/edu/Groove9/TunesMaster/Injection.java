/*
 * Copyright (C) 2015 The Android Open Source Project
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

package edu.Groove9.TunesMaster;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Context;
import android.support.annotation.NonNull;

import edu.Groove9.TunesMaster.addedittask.domain.usecase.DeleteTask;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.GetTask;
import edu.Groove9.TunesMaster.addedittask.domain.usecase.SaveTask;
import edu.Groove9.TunesMaster.data.FakeSongsRemoteDataSource;
import edu.Groove9.TunesMaster.data.source.SongsDataSource;
import edu.Groove9.TunesMaster.data.source.SongsRepository;
import edu.Groove9.TunesMaster.data.source.local.PrototypeSongsLocalDataSource;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.LastSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.NextSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.PlayPauseSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.RepeatSong;
import edu.Groove9.TunesMaster.songplayer.domain.usecase.ShuffleSong;
import edu.Groove9.TunesMaster.songplayer.player.AudioPlayerContract;
import edu.Groove9.TunesMaster.songplayer.player.PrototypeAudioPlayer;
import edu.Groove9.TunesMaster.statistics.domain.usecase.GetStatistics;
import edu.Groove9.TunesMaster.playlist.domain.filter.FilterFactory;
import edu.Groove9.TunesMaster.playlist.domain.usecase.GetTasks;

/**
 * Enables injection of mock implementations for
 * {@link SongsDataSource} at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static SongsRepository provideSongsRepository(@NonNull Context context) {
        checkNotNull(context);
        return SongsRepository.getInstance(FakeSongsRemoteDataSource.getInstance(),
                PrototypeSongsLocalDataSource.getInstance(context));
    }


    public static AudioPlayerContract provideAudioPlayer(@NonNull Context context) {
        checkNotNull(context);
        return PrototypeAudioPlayer.getInstance(context);
    }

    public static GetTasks provideGetTasks(@NonNull Context context) {
        return new GetTasks(provideSongsRepository(context), new FilterFactory());
    }

    public static UseCaseHandler provideUseCaseHandler() {
        return UseCaseHandler.getInstance();
    }

    public static GetTask provideGetTask(@NonNull Context context) {
        return new GetTask(Injection.provideSongsRepository(context));
    }

    public static SaveTask provideSaveTask(@NonNull Context context) {
        return new SaveTask(Injection.provideSongsRepository(context));
    }

    public static DeleteTask provideDeleteTask(@NonNull Context context) {
        return new DeleteTask(Injection.provideSongsRepository(context));
    }

    public static GetStatistics provideGetStatistics(@NonNull Context context) {
        return new GetStatistics(Injection.provideSongsRepository(context));
    }

    public static PlayPauseSong providePlayPauseSong(@NonNull Context context) {
        return new PlayPauseSong(Injection.provideAudioPlayer(context));
    }

    public static NextSong provideNextSong(@NonNull Context context) {
        return new NextSong(Injection.provideAudioPlayer(context));
    }

    public static LastSong provideLastSong(@NonNull Context context) {
        return new LastSong(Injection.provideAudioPlayer(context));
    }

    public static ShuffleSong provideShuffleSong(@NonNull Context context) {
        return new ShuffleSong(Injection.provideAudioPlayer(context));
    }
    public static RepeatSong provideRepeatSong(@NonNull Context context) {
        return new RepeatSong(Injection.provideAudioPlayer(context));
    }
}
