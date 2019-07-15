package com.example.spellingfrequency.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.spellingfrequency.R;
import com.example.spellingfrequency.database.AppDatabase;
import com.example.spellingfrequency.database.entity.CurrentWeightEntity;
import com.example.spellingfrequency.database.entity.EnglishWordEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
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
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if(currentUser== null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Login Required");

            final LayoutInflater factory = LayoutInflater.from(context);
            final View popupInputDialogView = factory.inflate(R.layout.popupemailpasswordlayout, null);
            builder.setView(popupInputDialogView);
            builder.setPositiveButton(android.R.string.ok, null); //Set to null. We override the onclick
            builder.setNegativeButton(android.R.string.cancel, null);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    final Button loginButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    loginButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            final EditText emailEditText = popupInputDialogView.findViewById(R.id.emailInput);
                            final EditText passEditText = popupInputDialogView.findViewById(R.id.passwordInput);
                            emailEditText.setEnabled(false);
                            passEditText.setEnabled(false);
                            loginButton.setEnabled(false);
                            String email = emailEditText.getText().toString();
                            String password = passEditText.getText().toString();
                            if (!email.equals("") && !password.equals("")){

                                mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    alertDialog.dismiss();
                                                    synchronize(context);

                                                } else {
                                                    // If sign in fails, display a message to the user.
                                                    Log.w(TAG, "signInWithEmail:failure", task.getException());

                                                    emailEditText.setEnabled(true);
                                                    passEditText.setEnabled(true);
                                                    loginButton.setEnabled(true);
                                                    passEditText.getText().clear();
                                                    try
                                                    {
                                                        throw task.getException();
                                                    } catch (FirebaseAuthInvalidUserException firebaseAuthInvalidUserException) {
                                                        Toast.makeText(context, "Authentication failed.\nInvalid email",
                                                                Toast.LENGTH_SHORT).show();
                                                    } catch (FirebaseAuthInvalidCredentialsException firebaseAuthInvalidCredentialsException){
                                                        Toast.makeText(context, "Authentication failed.\nInvalid password",
                                                                Toast.LENGTH_SHORT).show();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                            }
                                        });

                            }
                        }
                    });
                }
            });


            alertDialog.show();
            return;
        }
        final String uID = currentUser.getUid();
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Synchronizing...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final AppDatabase db = AppDatabase.getDatabase(context);
        final FirebaseFirestore fdb = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .build();
        fdb.setFirestoreSettings(settings);

        DocumentReference docRef = fdb.collection("counts").document(uID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            long changedFromRemote = document.getLong("changedCount");
                            if (changedFromRemote < getContChanged(db)) {
                                push(context, fdb, db, progressDialog, uID);
                            } else if (changedFromRemote > getContChanged(db)) {

                                pull(context, fdb, db, progressDialog, uID);
                            } else {
                                progressDialog.dismiss();

                                SharedPreferences sharedPref = context.getSharedPreferences(
                                        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                if (!sharedPref.getBoolean("dbModified", false))
                                    Toast.makeText(context, "no change found", Toast.LENGTH_SHORT).show();
                                else {
                                    push(context, fdb, db, progressDialog, uID);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean("dbModified", false);
                                    editor.apply();
                                }

                            }
                        } else {
                            Log.d(TAG, "No such document");
                            push(context, fdb, db, progressDialog, uID);
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }


    private static void pull(final Context context, FirebaseFirestore fdb, final AppDatabase db, final ProgressDialog progressDialog, String uID) {
        progressDialog.setMessage("downloading from server...");
        DocumentReference jsonFromRemote = fdb.collection("backups").document(uID);
        jsonFromRemote.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                            progressDialog.setMessage("total changes: " + mergedData.englishWordEntities.length + " words\nupdated: " + i + 1 + " word(s)");
                            EnglishWordEntity wordEntity = mergedData.englishWordEntities[i];
                            db.englishWordDao().updateWeightFabRepeat(wordEntity.getId(), wordEntity.getWeight(), wordEntity.getRepeat(), wordEntity.isFavourite());
                        }
                        progressDialog.dismiss();
                        Toast.makeText(context, mergedData.englishWordEntities.length + " local words is updated", Toast.LENGTH_SHORT).show();

                    }
                }
                ((Activity) context).recreate();
            }
        });
    }

    private static void push(Context context, FirebaseFirestore fdb, final AppDatabase db, ProgressDialog progressDialog, String uID) {
        progressDialog.setMessage("Uploading to server...");
        String json = generateJson(db);
        int contChanged = getContChanged(db);
        CollectionReference jsonCollectionRef = fdb.collection("backups");
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("json", json);
        jsonCollectionRef.document(uID).set(jsonMap);

        CollectionReference countFromRemote = fdb.collection("counts");
        Map<String, Object> countMap = new HashMap<>();
        countMap.put("changedCount", contChanged);
        countFromRemote.document(uID).set(countMap);
        progressDialog.setMessage("changes is uploaded to remote server");
        progressDialog.dismiss();
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

        MergedData(CurrentWeightEntity[] currentWeightEntities, EnglishWordEntity[] englishWordEntities) {
            this.currentWeightEntities = currentWeightEntities;
            this.englishWordEntities = englishWordEntities;
        }

    }
}
