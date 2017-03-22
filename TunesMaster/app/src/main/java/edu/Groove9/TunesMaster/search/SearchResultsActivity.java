package edu.Groove9.TunesMaster.search;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import edu.Groove9.TunesMaster.DatabaseTable;

/**
 * Created by Raktima on 3/6/2017.
 */

public class SearchResultsActivity extends Activity {
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        handleIntent(getIntent());
    }
    DatabaseTable db = new DatabaseTable(this);

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Cursor c = db.getWordMatches(query, null);
            //process Cursor and display results
            Context context = getApplicationContext();
            Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(c));
            c.moveToFirst();
            Toast.makeText(context,DatabaseUtils.dumpCursorToString(c),Toast.LENGTH_SHORT).show();

        }
    }
}
