package com.dcmmoguls.hardinglaw;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Button btnChat = (Button)findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AdminHomeActivity.this, ChannelListActivity.class);
                startActivity(myIntent);
            }
        });

        Button btnPush = (Button)findViewById(R.id.btnPush);
        btnPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, MessagesActivity.class);
                intent.putExtra("chatting", false);
                startActivity(intent);
            }
        });
    }
}
