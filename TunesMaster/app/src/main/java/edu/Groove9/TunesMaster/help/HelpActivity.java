package edu.Groove9.TunesMaster.help;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.Groove9.TunesMaster.Injection;
import edu.Groove9.TunesMaster.R;
import edu.Groove9.TunesMaster.songplayer.SongPlayerFragment;
import edu.Groove9.TunesMaster.util.ActivityUtils;


public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_act);

        //get Help text
        String helpText = (String)getIntent().getSerializableExtra("Help_Text");

        //Set up fragment
        HelpFragment helpFragment = (HelpFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (helpFragment == null) {
            helpFragment = HelpFragment.newInstance();

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    helpFragment, R.id.contentFrame);
        }

        //create presenter
        HelpPresenter helpPresenter = new HelpPresenter(
                Injection.provideUseCaseHandler(),
                helpText,
                (HelpContract.View) helpFragment
        );
        //fragment.setPresenter(presenter)
        helpFragment.setPresenter(helpPresenter);
    }

}
