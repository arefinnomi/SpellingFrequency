package com.example.spellingfrequency.database;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.spellingfrequency.R;
import com.example.spellingfrequency.database.dao.EnglishWordDao;
import com.example.spellingfrequency.database.entity.AntonymEntity;
import com.example.spellingfrequency.database.entity.BanglaWordEntity;
import com.example.spellingfrequency.database.entity.CurrentWeightEntity;
import com.example.spellingfrequency.database.entity.EnglishWordEntity;
import com.example.spellingfrequency.database.entity.SynonymEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DatabaseInitializer {

    private static final String TAG = "mytag";

    public static void populateAsync(Activity context, @NonNull final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db, context);
        task.execute();
    }

    private static EnglishWordEntity[] addEnglishWord(final AppDatabase db, EnglishWordEntity[] englishWordEntities) {
        db.englishWordDao().insertAll(englishWordEntities);
        return englishWordEntities;
    }

    private static BanglaWordEntity[] addBanglaWord(final AppDatabase db, BanglaWordEntity[] banglaWordEntities) {
        db.banglaWordDao().insertAll(banglaWordEntities);
        return banglaWordEntities;
    }

    private static SynonymEntity[] addSynonym(final AppDatabase db, SynonymEntity[] synonymEntities) {
        db.synonymDao().insertAll(synonymEntities);
        return synonymEntities;
    }

    private static AntonymEntity[] addAntonym(final AppDatabase db, AntonymEntity[] antonymEntities) {
        db.antonymDao().insertAll(antonymEntities);
        return antonymEntities;
    }

    private static void nukeAll(final AppDatabase db) {
        db.currentWeightDao().nukeTable();
        db.synonymDao().nukeTable();
        db.antonymDao().nukeTable();
        db.banglaWordDao().nukeTable();
        db.englishWordDao().nukeTable();
    }


    private static void populateWithTestData(Context context, AppDatabase db) throws IOException, JSONException {
        nukeAll(db);
        Log.d(TAG, "populateWithTestData: english: " + db.englishWordDao().loadAllEnglishWord().length);
        Log.d(TAG, "populateWithTestData: bangla: " + db.banglaWordDao().loadAllBanglaWord().length);
        Log.d(TAG, "populateWithTestData: synonym: " + db.synonymDao().loadAllSynonym().length);
        Log.d(TAG, "populateWithTestData: antonym: " + db.antonymDao().loadAllAntonym().length);
        Log.d(TAG, "populateWithTestData: CurrentWeight" + db.currentWeightDao().loadAllCurrentWeight().length);
        //load assets/dictionay,json
        String json = getStringFromAsset(context);

        Log.d(TAG, "populateWithTestData: file loaded");

        //store english word in database
        JSONObject jsonObjects = new JSONObject(json);
        EnglishWordEntity[] englishWordEntities = getEnglishWordEntitiesFromJson(jsonObjects);

        addEnglishWord(db, englishWordEntities);
        Log.d(TAG, "populateWithTestData: english word loaded");

        //store bangla word in database
        englishWordEntities = db.englishWordDao().loadAllEnglishWord();
        BanglaWordEntity[] banglaWordEntities = getBanglaWordEntitiesFromJson(jsonObjects, englishWordEntities);
        addBanglaWord(db, banglaWordEntities);
        Log.d(TAG, "populateWithTestData: bangla word added");

        //store synonym in database
        SynonymEntity[] synonymEntities = getSynonymEntitiesFromJson(jsonObjects, englishWordEntities);
        addSynonym(db, synonymEntities);
        Log.d(TAG, "populateWithTestData: synonym added");

        //store synonym in database
        AntonymEntity[] antonymEntities = getAntonymEntitiesFromJson(jsonObjects, englishWordEntities);
        addAntonym(db, antonymEntities);
        Log.d(TAG, "populateWithTestData: antonym added" + db.antonymDao().loadAllAntonym().length);

        //store max min weight
        EnglishWordDao.WeightPojo maxWeight = db.englishWordDao().maxWeight();
        EnglishWordDao.WeightPojo minWeight = db.englishWordDao().minWeight();
        CurrentWeightEntity currentMaxWeightEntity = new CurrentWeightEntity("max_weight", maxWeight.weight);
        CurrentWeightEntity currentMinWeightEntity = new CurrentWeightEntity("min_weight", minWeight.weight);
        db.currentWeightDao().insertAll(currentMaxWeightEntity, currentMinWeightEntity);
        Log.d(TAG, "populateWithTestData: CurrentWeight" + db.currentWeightDao().loadAllCurrentWeight().length);
    }

    private static SynonymEntity[] getSynonymEntitiesFromJson(JSONObject jsonObjects, EnglishWordEntity[] englishWordEntities) throws JSONException {
        Map<String, EnglishWordEntity> englishWordEntityMap = new HashMap<>();
        for (EnglishWordEntity englishWordEntity : englishWordEntities) {
            englishWordEntityMap.put(englishWordEntity.getText(), englishWordEntity);
        }
        ArrayList<SynonymEntity> synonymEntityArrayList = new ArrayList<>();
        for (EnglishWordEntity englishWordEntity : englishWordEntities) {
            JSONObject enInnerObject = jsonObjects.getJSONObject(englishWordEntity.getText());
            JSONObject synonymsObject = enInnerObject.getJSONObject("syn");
            int synonymArrayLen = synonymsObject.length();
            for (Iterator iterator = synonymsObject.keys(); iterator.hasNext(); ) {
                String synonym = (String) iterator.next();
                if (!englishWordEntityMap.containsKey(synonym)) continue;
                synonymEntityArrayList.add(new SynonymEntity(englishWordEntity.getId(), englishWordEntityMap.get(synonym).getId(), synonymsObject.getInt(synonym)));
            }
        }

        SynonymEntity[] synonymEntities = new SynonymEntity[synonymEntityArrayList.size()];
        synonymEntities = synonymEntityArrayList.toArray(synonymEntities);
        return synonymEntities;
    }


    private static AntonymEntity[] getAntonymEntitiesFromJson(JSONObject jsonObjects, EnglishWordEntity[] englishWordEntities) throws JSONException {
        Map<String, EnglishWordEntity> englishWordEntityMap = new HashMap<>();
        for (EnglishWordEntity englishWordEntity : englishWordEntities) {
            englishWordEntityMap.put(englishWordEntity.getText(), englishWordEntity);
        }
        ArrayList<AntonymEntity> antonymEntityArrayList = new ArrayList<>();
        for (EnglishWordEntity englishWordEntity : englishWordEntities) {
            JSONObject enInnerObject = jsonObjects.getJSONObject(englishWordEntity.getText());
            JSONArray antonymArray = enInnerObject.getJSONArray("ant");
            int AntonymArrayLen = antonymArray.length();
            for (int j = 0; j < AntonymArrayLen; j++) {
                String antonym = antonymArray.getString(j);
                if (!englishWordEntityMap.containsKey(antonym)) continue;
                antonymEntityArrayList.add(new AntonymEntity(englishWordEntity.getId(), englishWordEntityMap.get(antonym).getId()));
            }
        }

        AntonymEntity[] antonymEntities = new AntonymEntity[antonymEntityArrayList.size()];
        antonymEntities = antonymEntityArrayList.toArray(antonymEntities);
        return antonymEntities;
    }

    private static BanglaWordEntity[] getBanglaWordEntitiesFromJson(JSONObject jsonObjects, EnglishWordEntity[] englishWordEntitys) throws JSONException {
        ArrayList<BanglaWordEntity> banglaWordEntitiesArrayList = new ArrayList<>();
        for (EnglishWordEntity englishWordEntity : englishWordEntitys) {
            JSONObject enInnerObject = jsonObjects.getJSONObject(englishWordEntity.getText());
            JSONArray bnArray = enInnerObject.getJSONArray("bn");
            int bnArrayLen = bnArray.length();
            for (int j = 0; j < bnArrayLen; j++) {
                banglaWordEntitiesArrayList.add(new BanglaWordEntity(bnArray.getString(j), englishWordEntity.getId()));
            }
        }
        BanglaWordEntity[] banglaWordEntities = new BanglaWordEntity[banglaWordEntitiesArrayList.size()];
        banglaWordEntities = banglaWordEntitiesArrayList.toArray(banglaWordEntities);
        return banglaWordEntities;
    }

    private static EnglishWordEntity[] getEnglishWordEntitiesFromJson(JSONObject jsonObjects) throws JSONException {
        int i = 0;
        EnglishWordEntity[] englishWordEntities = new EnglishWordEntity[jsonObjects.length()];
        for (Iterator iterator = jsonObjects.keys(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            EnglishWordEntity englishWordEntity = new EnglishWordEntity(key, 1, jsonObjects.getJSONObject(key).getLong("freq"));
            englishWordEntities[i++] = englishWordEntity;
//            Log.d("mytag", "populateWithTestData: "+i+": "+englishWordEntity.getText());

        }
        Arrays.sort(englishWordEntities, new EnglishWordEntity.Compare());
        for (i = 0; i < englishWordEntities.length; i++) {
            englishWordEntities[i].setWeight(i + 1);
        }
        return englishWordEntities;
    }

    private static String getStringFromAsset(Context context) {
        String json = null;
        try {
            InputStream inputStream = context.getAssets().open("dictionary.json");
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
            json = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final AppDatabase mDb;
        private final Activity context;
        ProgressDialog progressDialog;

        PopulateDbAsync(AppDatabase db, Activity context) {
            mDb = db;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                populateWithTestData(this.context, mDb);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();

            progressDialog = new ProgressDialog(this.context);
            progressDialog.setMessage("Building Database...");
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("init", true);
            editor.putBoolean("dbModified", false);
            editor.putBoolean("lastWordMisspelled", false);
            editor.putBoolean("lastWordSaved", true);
            editor.putBoolean("lastWordInputDisabled", false);
            editor.apply();
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            context.recreate();

        }


    }
}