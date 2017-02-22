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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.containsString;

import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import edu.Groove9.TunesMaster.R;
import edu.Groove9.TunesMaster.data.FakeTasksRemoteDataSource;

import edu.Groove9.TunesMaster.playlist.domain.model.Song;
import edu.Groove9.TunesMaster.data.source.TasksRepository;
import edu.Groove9.TunesMaster.songplayer.SongPlayerActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for the statistics screen.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class StatisticsScreenTest {

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     *
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public ActivityTestRule<StatisticsActivity> mStatisticsActivityTestRule =
            new ActivityTestRule<>(StatisticsActivity.class, true, false);

    /**
     * Setup your test fixture with a fake task id. The {@link SongPlayerActivity} is started with
     * a particular task id, which is then loaded from the service API.
     *
     * <p>
     * Note that this test runs hermetically and is fully isolated using a fake implementation of
     * the service API. This is a great way to make your tests more reliable and faster at the same
     * time, since they are isolated from any outside dependencies.
     */
    @Before
    public void intentWithStubbedTaskId() {
        // Given some tasks
        TasksRepository.destroyInstance();
        FakeTasksRemoteDataSource.getInstance().addTasks(new Song("Title1", "", false, Uri.parse("https://www.youtube.com/watch?v=4PDJcw9oJt0")));
        FakeTasksRemoteDataSource.getInstance().addTasks(new Song("Title2", "", true, Uri.parse("https://www.youtube.com/watch?v=4PDJcw9oJt0")));

        // Lazily start the Activity from the ActivityTestRule
        Intent startIntent = new Intent();
        mStatisticsActivityTestRule.launchActivity(startIntent);
        /**
         * Prepare your test fixture for this test. In this case we register an IdlingResources with
         * Espresso. IdlingResource resource is a great way to tell Espresso when your app is in an
         * idle state. This helps Espresso to synchronize your test actions, which makes tests significantly
         * more reliable.
         */
        Espresso.registerIdlingResources(
                mStatisticsActivityTestRule.getActivity().getCountingIdlingResource());
    }

    @Test
    public void Tasks_ShowsNonEmptyMessage() throws Exception {
        // Check that the active and completed tasks text is displayed
        String expectedActiveTaskText = InstrumentationRegistry.getTargetContext()
                .getString(R.string.statistics_active_tasks);
        onView(withText(containsString(expectedActiveTaskText))).check(matches(isDisplayed()));
        String expectedCompletedTaskText = InstrumentationRegistry.getTargetContext()
                .getString(R.string.statistics_completed_tasks);
        onView(withText(containsString(expectedCompletedTaskText))).check(matches(isDisplayed()));
    }
}
