package com.example.spellingfrequency.UI;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spellingfrequency.R;
import com.example.spellingfrequency.database.AppDatabase;
import com.example.spellingfrequency.database.entity.BanglaWordEntity;
import com.example.spellingfrequency.database.entity.EnglishWordEntity;
import com.example.spellingfrequency.model.Word;

import java.util.Map;

public class WordDetailsActivity extends AppCompatActivity {

    Word word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_details);

        Toolbar toolbar = findViewById(R.id.toolbar_word_details);
        setSupportActionBar(toolbar);

        String wordString = getIntent().getStringExtra("wordString");
        final AppDatabase appDatabase = AppDatabase.getDatabase(this);
        word = new Word(appDatabase);
        word.loadWord(wordString);
        load_word_to_view();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word_details, menu);
        if (!word.isFavorite())
            menu.findItem(R.id.action_favorite_word_details).setIcon(R.drawable.ic_favorite_black);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite_word_details: {
                word.saveFavorite(!word.isFavorite());
                if (word.isFavorite()) {
                    item.setIcon(R.drawable.ic_favorite_red);
                    Toast.makeText(this, "added to favorite", Toast.LENGTH_SHORT).show();
                } else {
                    item.setIcon(R.drawable.ic_favorite_black);
                    Toast.makeText(this, "removed from favorite", Toast.LENGTH_SHORT).show();
                }
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void load_word_to_view() {
        TextView wordTextView = findViewById(R.id.wordDetails_word_textView);
        wordTextView.setText(word.getWord());
        StringBuilder temp = new StringBuilder("Bangla: ");
        boolean flagFirstWord = true;
        for (BanglaWordEntity banglaWordEntity : word.getBanglaWords()) {

            if (flagFirstWord) {
                flagFirstWord = false;

            } else {
                temp.append(", ");
            }
            temp.append(banglaWordEntity.getText());

        }
        temp.append("\n\nSynonym:");
        for (Map.Entry<EnglishWordEntity, BanglaWordEntity[]> synonymWord : word.getSynonymWordsWithBangla().entrySet()) {
            temp.append("\n");
            temp.append(synonymWord.getKey().getText()).append(": ");
            flagFirstWord = true;
            for (BanglaWordEntity banglaWordEntity : synonymWord.getValue()) {
                if (flagFirstWord) {
                    flagFirstWord = false;

                } else {
                    temp.append(", ");
                }
                temp.append(banglaWordEntity.getText());
            }
            temp.append(" ");
        }

        temp.append("\n\nAntonym:");
        for (Map.Entry<EnglishWordEntity, BanglaWordEntity[]> antonymWord : word.getAntonymWordsWithBangla().entrySet()) {
            temp.append("\n");
            temp.append(antonymWord.getKey().getText()).append(": ");
            flagFirstWord = true;
            for (BanglaWordEntity banglaWordEntity : antonymWord.getValue()) {
                if (flagFirstWord) {
                    flagFirstWord = false;

                } else {
                    temp.append(", ");
                }
                temp.append(banglaWordEntity.getText());
            }
            temp.append(" ");
        }
        TextView translationTextView = findViewById(R.id.wordDetails_translation_textview);
        translationTextView.setText(temp.toString());
        translationTextView.setMovementMethod(new ScrollingMovementMethod());

    }


}
