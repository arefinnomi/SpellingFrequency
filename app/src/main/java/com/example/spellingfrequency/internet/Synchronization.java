package com.example.spellingfrequency.internet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.spellingfrequency.R;
import com.example.spellingfrequency.database.AppDatabase;
import com.example.spellingfrequency.database.entity.CurrentWeightEntity;
import com.example.spellingfrequency.database.entity.EnglishWordEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Synchronization {

    public static void synchronize(final Context context) {
        final ProgressDialog progDailog = new ProgressDialog(context);
        progDailog.setMessage("Synchronizing...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);
        progDailog.show();

        final AppDatabase db = AppDatabase.getDatabase(context);
        final FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        fdb.setFirestoreSettings(settings);

        DocumentReference docRef = fdb.collection("counts").document("arefin");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            long fchanged = document.getLong("changedCount");
                            if (fchanged < getContChanged(db)) {
                                push(context, fdb, db, progDailog);
                            } else if (fchanged > getContChanged(db)) {

                                pull(context, fdb, db, progDailog);
                            } else {
                                progDailog.dismiss();

                                SharedPreferences sharedPref = context.getSharedPreferences(
                                        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                if (!sharedPref.getBoolean("dbModified", false))
                                    Toast.makeText(context, "no change found", Toast.LENGTH_SHORT).show();
                                else {
                                    push(context, fdb, db, progDailog);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean("dbModified", false);
                                    editor.apply();
                                }

                            }
                        } else {
                            Log.d(TAG, "No such document");
                            push(context, fdb, db, progDailog);
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private static void pull(final Context context, FirebaseFirestore fdb, final AppDatabase db, final ProgressDialog progDailog) {
        progDailog.setMessage("downloading from server...");
        DocumentReference fjson = fdb.collection("backups").document("arefin");
        fjson.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    if (document.exists()) {
                        String json = document.getString("json");
                        Gson gson = new Gson();
                        MergedData mergedData = gson.fromJson(json, MergedData.class);
//                        Log.d(TAG, "onComplete: "+json);
                        for (int i = 0; i < mergedData.currentWeightEntities.length; i++) {
                            db.currentWeightDao().updateCurrentWeight(mergedData.currentWeightEntities[i]);
                        }
                        for (int i = 0; i < mergedData.englishWordEntities.length; i++) {
                            progDailog.setMessage("total changes: " + mergedData.englishWordEntities.length + " words\nupdated: " + i + 1 + " word(s)");
                            EnglishWordEntity wordEntity = mergedData.englishWordEntities[i];
                            db.englishWordDao().updateWeightFabRepeat(wordEntity.getId(), wordEntity.getWeight(), wordEntity.getRepeat(), wordEntity.isFavourite());
                        }
                        progDailog.dismiss();
                        Toast.makeText(context, mergedData.englishWordEntities.length + " local words is updated", Toast.LENGTH_SHORT).show();

                    }
                }
                ((Activity) context).recreate();
            }
        });
    }

    private static void push(Context context, FirebaseFirestore fdb, final AppDatabase db, ProgressDialog progDailog) {
        progDailog.setMessage("Uploading to server...");
        String json = generateJson(db);
        int contChanged = getContChanged(db);
        CollectionReference fjson = fdb.collection("backups");
        Map<String, Object> datajson = new HashMap<>();
        datajson.put("json", json);
        fjson.document("arefin").set(datajson);

        CollectionReference fcount = fdb.collection("counts");
        Map<String, Object> datacount = new HashMap<>();
        datacount.put("changedCount", contChanged);
        fcount.document("arefin").set(datacount);
        progDailog.setMessage("changes is uploaded to remote server");
        progDailog.dismiss();
        Toast.makeText(context, "remote server is updated", Toast.LENGTH_SHORT).show();
    }

    private static int getContChanged(AppDatabase db) {
        return db.englishWordDao().countStudiedEnglishWord();
    }

    private static String generateJson(@NonNull final AppDatabase db) {
        MergedData mergedData = new MergedData(db.currentWeightDao().loadAllCurrentWeight(), db.englishWordDao().loadAllChanged());
        Gson gson = new Gson();
        return gson.toJson(mergedData);
    }

    private static class MergedData {
        CurrentWeightEntity[] currentWeightEntities;
        EnglishWordEntity[] englishWordEntities;

        public MergedData(CurrentWeightEntity[] currentWeightEntities, EnglishWordEntity[] englishWordEntities) {
            this.currentWeightEntities = currentWeightEntities;
            this.englishWordEntities = englishWordEntities;
        }

        public CurrentWeightEntity[] getCurrentWeightEntities() {
            return currentWeightEntities;
        }

        public void setCurrentWeightEntities(CurrentWeightEntity[] currentWeightEntities) {
            this.currentWeightEntities = currentWeightEntities;
        }

        public EnglishWordEntity[] getEnglishWordEntities() {
            return englishWordEntities;
        }

        public void setEnglishWordEntities(EnglishWordEntity[] englishWordEntities) {
            this.englishWordEntities = englishWordEntities;
        }
    }
}
