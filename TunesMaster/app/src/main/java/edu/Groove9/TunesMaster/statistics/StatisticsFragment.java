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

package edu.Groove9.TunesMaster.statistics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.Groove9.TunesMaster.R;
import edu.Groove9.TunesMaster.statistics.domain.model.UserSession;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main UI for the statistics screen.
 */
public class StatisticsFragment extends Fragment implements StatisticsContract.View {

    private EditText userIdET;
    private Button endSession;
    private Button startSession;

    private StatisticsContract.Presenter mPresenter;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
    }

    @Override
    public void setPresenter(@NonNull StatisticsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.statistics_frag, container, false);
        userIdET = (EditText) root.findViewById(R.id.user_id);
        userIdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                showUserSessionStatus();
            }
        });
        endSession = (Button) root.findViewById(R.id.end_session_btn);
        endSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSession.end();
                showUserSessionStatus();
            }
        });
        startSession = (Button) root.findViewById(R.id.start_session_btn);
        startSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUserId = userIdET.getText().toString();
                UserSession.start(newUserId);
                showUserSessionStatus();
            }
        });

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        showUserSessionStatus();
    }

    @Override
    public void showUserSessionStatus() {
        UserSession userSession = UserSession.get();
        if (userSession == null) {
            endSession.setEnabled(false);
            String newUserId = userIdET.getText().toString();
            startSession.setEnabled(UserSession.isUserIdValid(newUserId));
        } else {
            endSession.setEnabled(true);
            String newUserId = userIdET.getText().toString();
            if (!newUserId.equals(userSession.userId)) {
                userIdET.setText(userSession.userId);
            }
            startSession.setEnabled(false);
        }
    }
}
