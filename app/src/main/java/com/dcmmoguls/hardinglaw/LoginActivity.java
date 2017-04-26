package com.dcmmoguls.hardinglaw;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dcmmoguls.hardinglaw.model.MyUser;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText etName;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static String TAG = "User Signin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        etName = (EditText) findViewById(R.id.editText);

        final SharedPreferences sharedPref = getSharedPreferences("com.dcmmoguls.hardinglaw", Context.MODE_PRIVATE);
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users/"+FirebaseAuth.getInstance().getCurrentUser().getUid());

// Attach a listener to read the data at our posts reference
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) dataSnapshot.getValue();

                    if (obj!= null && obj.containsKey("name") && obj.containsKey("isAdmin")) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.putBoolean("isAdmin", (Boolean) obj.get("isAdmin"));
                        editor.putString("user_name", (String)obj.get("name"));
                        editor.commit();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

        if(sharedPref.contains("isAdmin")) {
            if(sharedPref.getBoolean("isAdmin", false))
            {
                Intent myIntent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                startActivity(myIntent);
                finish();
            } else {
                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(myIntent);
                finish();
            }
        }

        ImageButton btnStart = (ImageButton) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void login() {
        if(etName.getText().toString().equals("")) {
            new AlertDialog.Builder(this)
                    .setTitle("Input Error")
                    .setMessage("Please enter your name")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })

                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
            return;
        }

        final CircularProgressView progressView = (CircularProgressView) findViewById(R.id.progress_view);
        progressView.startAnimation();
        progressView.setVisibility(View.VISIBLE);
        mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                        progressView.stopAnimation();
                        progressView.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else if(task.getResult().getUser() != null) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid());
                            HashMap<String, Object> data = new HashMap<String, Object>();
                            data.put("isAdmin", false);
                            data.put("name", etName.getText().toString());
                            data.put("OneSignalId", "");
                            ref.setValue(data);

                            DatabaseReference channelRef = FirebaseDatabase.getInstance().getReference().child("channels").child(task.getResult().getUser().getUid());
                            HashMap<String, Object> channelData = new HashMap<String, Object>();
                            channelData.put("name", etName.getText().toString());
                            channelRef.setValue(channelData);

                            SharedPreferences sharedPref = getSharedPreferences("com.dcmmoguls.hardinglaw", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("userid", task.getResult().getUser().getUid());
                            editor.putBoolean("isAdmin", false);
                            editor.putString("user_name", etName.getText().toString());
                            editor.commit();

                            Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(myIntent);
                            finish();
                        }

                        // ...
                    }
                });
    }
}
