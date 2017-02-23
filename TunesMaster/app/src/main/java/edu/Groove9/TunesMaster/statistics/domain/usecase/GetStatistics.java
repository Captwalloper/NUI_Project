package edu.Groove9.TunesMaster.statistics.domain.usecase;

import android.support.annotation.NonNull;

import edu.Groove9.TunesMaster.UseCase;
import edu.Groove9.TunesMaster.data.source.SongsRepository;
import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.data.source.SongsDataSource;
import edu.Groove9.TunesMaster.statistics.domain.model.Statistics;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Calculate statistics of active and completed Tasks {@link Song} in the {@link SongsRepository}.
 */
public class GetStatistics extends UseCase<GetStatistics.RequestValues, GetStatistics.ResponseValue> {

    private final SongsRepository mSongsRepository;

    public GetStatistics(@NonNull SongsRepository songsRepository) {
        mSongsRepository = checkNotNull(songsRepository, "songsRepository cannot be null!");
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {
        mSongsRepository.getSongs(new SongsDataSource.LoadSongsCallback() {
            @Override
            public void onSongsLoaded(List<Song> songs) {

                int activeTasks = 0;
                int completedTasks = 0;

                // We calculate number of active and completed songs
                for (Song song : songs) {
//                    if (song.isCompleted()) {
                        completedTasks += 1;
//                    } else {
                        activeTasks += 1;
//                    }
                }

                ResponseValue responseValue = new ResponseValue(new Statistics(completedTasks, activeTasks));
                getUseCaseCallback().onSuccess(responseValue);
            }

            @Override
            public void onDataNotAvailable() {
                getUseCaseCallback().onError();
            }
        });
    }

    public static class RequestValues implements UseCase.RequestValues {
    }

    public static class ResponseValue implements UseCase.ResponseValue {

        private final Statistics mStatistics;

        public ResponseValue(@NonNull Statistics statistics) {
            mStatistics = checkNotNull(statistics, "statistics cannot be null!");
        }

        public Statistics getStatistics() {
            return mStatistics;
        }
    }
}
