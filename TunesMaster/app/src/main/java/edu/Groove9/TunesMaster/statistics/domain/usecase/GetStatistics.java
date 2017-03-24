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


    public GetStatistics(@NonNull SongsRepository songsRepository) {
    }

    @Override
    protected void executeUseCase(RequestValues requestValues) {

    }

    public static class RequestValues implements UseCase.RequestValues {
    }

    public static class ResponseValue implements UseCase.ResponseValue {

    }
}
