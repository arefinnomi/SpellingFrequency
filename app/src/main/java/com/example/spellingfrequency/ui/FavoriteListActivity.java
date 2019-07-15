package com.example.spellingfrequency.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.spellingfrequency.R;
import com.example.spellingfrequency.database.AppDatabase;
import com.example.spellingfrequency.model.FavoriteWords;


public class FavoriteListActivity extends ListActivity {

    private ArrayAdapter<String> listDataAdapter;
    private FavoriteWords favoriteWords = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the activity layout xml file.
        setContentView(R.layout.activity_favorite_list);


        final AppDatabase appDatabase = AppDatabase.getDatabase(this);

        favoriteWords = new FavoriteWords(appDatabase);

        listDataAdapter = new ArrayAdapter<>(this, R.layout.activity_favorite_list_row, R.id.favorite_list_row_textView, favoriteWords.getEnglishWords());

        this.setListAdapter(listDataAdapter);
    }

    // When user click list item, this method will be invoked.
    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {

        Log.d("mytag", "onListItemClick: ");
        ListAdapter listAdapter = listView.getAdapter();
        // Get user selected item object.
        Object selectItemObj = listAdapter.getItem(position);
        String wordString = (String) selectItemObj;
        Intent intent = new Intent(getBaseContext(), WordDetailsActivity.class);
        intent.putExtra("wordString", wordString);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (favoriteWords != null) {
            favoriteWords.update();
            listDataAdapter.clear();
            listDataAdapter.addAll(favoriteWords.getEnglishWords());
        }
    }
}
