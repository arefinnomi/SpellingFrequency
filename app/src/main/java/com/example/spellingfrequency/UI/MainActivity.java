package com.example.spellingfrequency.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.spellingfrequency.R;
import com.example.spellingfrequency.database.AppDatabase;
import com.example.spellingfrequency.database.DatabaseInitializer;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppDatabase appDatabase = AppDatabase.getDatabase(this);
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE);

        if (!sharedPref.getBoolean("init", false)) {
            DatabaseInitializer.populateAsync(this, appDatabase);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
