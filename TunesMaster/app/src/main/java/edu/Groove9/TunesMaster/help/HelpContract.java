package edu.Groove9.TunesMaster.help;

import edu.Groove9.TunesMaster.BasePresenter;
import edu.Groove9.TunesMaster.BaseView;
import edu.Groove9.TunesMaster.songplayer.SongPlayerContract;

/**
 * Created by Raktima on 4/10/2017.
 */

public class HelpContract {
    interface View extends BaseView<Presenter> {
        void showHelpDescription(String description);
    }
    interface Presenter extends BasePresenter {
        void help();
        void start();
    }
}
