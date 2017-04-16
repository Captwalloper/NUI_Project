package edu.Groove9.TunesMaster.help;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.Groove9.TunesMaster.R;
import edu.Groove9.TunesMaster.playlist.PlaylistFragment;
import edu.Groove9.TunesMaster.songplayer.SongPlayerContract;
import edu.Groove9.TunesMaster.songplayer.SongPlayerFragment;

import static com.google.common.base.Preconditions.checkNotNull;


public class HelpFragment extends Fragment implements HelpContract.View {

    private HelpContract.Presenter mPresenter;
    private TextView mHelp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.help_frag, container, false);
        mHelp = (TextView) root.findViewById(R.id.help_text);
        return root;
    }

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void showHelpDescription(String description) {
        mHelp.setText(description);
    }

    @Override
    public void setPresenter(HelpContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }
}
