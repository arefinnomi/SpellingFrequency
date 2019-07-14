package com.example.spellingfrequency.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spellingfrequency.R;
import com.example.spellingfrequency.database.AppDatabase;
import com.example.spellingfrequency.database.entity.BanglaWordEntity;
import com.example.spellingfrequency.database.entity.EnglishWordEntity;
import com.example.spellingfrequency.internet.Synchronization;
import com.example.spellingfrequency.model.Statistics;
import com.example.spellingfrequency.model.Word;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class MainActivityFragment extends Fragment {
    /*var*/
    Word word = null;
    boolean nowWordDisplayed = false;
    boolean isWordDisplayed = false;
    boolean isUserMisspelled = false;
//    boolean isFavorite;

    /*view*/
    TextView wordTextView;
    Button listenButton;
    Button errorButton;
    Button masteredButton;
    TextToSpeech textToSpeech;
    TextView translationTextView;
    Button input_button;
    TextInputEditText speelingInputEditText;
    Statistics statistics;

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final AppDatabase appDatabase = AppDatabase.getDatabase(getContext());
        statistics = new Statistics(appDatabase);
        final SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), MODE_PRIVATE);

        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        setViewAndListener(appDatabase, sharedPref, view);
        load_word_to_view(appDatabase, sharedPref, view);

        return view;
    }

    private void setWordTextView() {
        wordTextView.setText(word.getWord());
        wordTextView.setTextColor(getResources().getColor(R.color.wordBlack));
        nowWordDisplayed = true;
    }

    private void resetWordTextView() {
        wordTextView.setText(R.string.defaultTextWordTextView);
        wordTextView.setTextColor(getResources().getColor(R.color.wordBlur));
        nowWordDisplayed = false;
        wordTextView.setEnabled(true);
    }

    private void resetAllView() {
        word = null;
        nowWordDisplayed = false;
        isWordDisplayed = false;
        isUserMisspelled = false;

        masteredButton.setEnabled(true);
        errorButton.setEnabled(true);
        speelingInputEditText.setEnabled(true);
        input_button.setEnabled(true);
        resetWordTextView();
        translationTextView.setText("");
        translationTextView.scrollTo(0, 0);
        speelingInputEditText.setText(null);
        hideKeyboard(getActivity());
    }

    void checkInputSpelling() {
        String userSpelling = speelingInputEditText.getText().toString();

        if (userSpelling.isEmpty()) return;
        isUserMisspelled = !word.getWord().equals(userSpelling.toLowerCase().trim());
        if (isUserMisspelled) {
            masteredButton.setEnabled(false);
            errorButton.setEnabled(true);
            Toast.makeText(getActivity(), "wrong spelling", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(getActivity(), "correct spelling", Toast.LENGTH_SHORT).show();
            String temp = Arrays.toString(Character.toChars(10004))
                    + userSpelling
                    + Arrays.toString(Character.toChars(10004));
            speelingInputEditText.setText(temp);
            speelingInputEditText.setEnabled(false);
            hideKeyboard(getActivity());
            setWordTextView();
            wordTextView.setEnabled(false);
        }

    }

    private void load_word_to_view(AppDatabase appDatabase, SharedPreferences sharedPref, View view) {
        if (sharedPref.getBoolean("init", false)) {
            resetAllView();
            word = new Word(appDatabase);
            word.loadNextWord();
            if (word != null && !textToSpeech.isSpeaking())
                textToSpeech.speak(word.getWord(), TextToSpeech.QUEUE_FLUSH, null);

            StringBuilder temp = new StringBuilder("Bangla:\n");
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
            translationTextView.setText(temp.toString());
//            isFavorite = word.isFavorite();
        }

    }

    private void setViewAndListener(final AppDatabase appDatabase, final SharedPreferences sharedPref, final View view) {
        translationTextView = view.findViewById(R.id.translation_textview);
        translationTextView.setMovementMethod(new ScrollingMovementMethod());

        wordTextView = view.findViewById(R.id.word_textView);
        wordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWordDisplayed = true;
                if (nowWordDisplayed) {
                    resetWordTextView();
                } else if (isUserMisspelled) {
                    setWordTextView();
                } else {
                    setWordTextView();
                    speelingInputEditText.setEnabled(false);
                    input_button.setEnabled(false);
                }
            }
        });
        masteredButton = view.findViewById(R.id.mastered_button);
        masteredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                word.saveCurrentWordStatus(true);
                load_word_to_view(appDatabase, sharedPref, view);
            }
        });

        errorButton = view.findViewById(R.id.error_button);
        errorButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isUserMisspelled) {
                    Toast.makeText(getActivity(), "Write correct spelling", Toast.LENGTH_SHORT).show();
                    return;
                }
                word.saveCurrentWordStatus(false);
                load_word_to_view(appDatabase, sharedPref, view);
            }
        });

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                    textToSpeech.setSpeechRate((float) .001);
                }
            }
        });

        listenButton = view.findViewById(R.id.listen_button);
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (word != null && !textToSpeech.isSpeaking())
                    textToSpeech.speak(word.getWord(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        speelingInputEditText = view.findViewById(R.id.spellingInput);

        input_button = view.findViewById(R.id.input_button);
        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputSpelling();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHasOptionsMenu(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:

                if (word.isFavorite()) {
                    Toast.makeText(getActivity(), "already in favorite", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "added to favorite", Toast.LENGTH_SHORT).show();
                    word.saveFavorite(!word.isFavorite());
                }
                return true;
            case R.id.action_statistics: {
                AlertDialog.Builder statisticsDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Statistics")
                        .setNegativeButton(android.R.string.ok, null);
                statistics.update();
                statisticsDialog.setMessage(statistics.toString());
                statisticsDialog.show();
                return true;
            }
            case R.id.action_show_favorites: {
                Intent intent = new Intent(getActivity(), FavoriteListActivity.class);
                startActivity(intent);
            }
            case R.id.action_sync: {
                Synchronization.synchronize(getContext());
            }
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);


        }
    }
}