package com.example.comicall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private ComicLoaderCallbacks comicLoaderCallbacks;
    private static final int COMIC_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent anteriorIntent = getIntent();
        Bundle bundle = anteriorIntent.getExtras();
        comicLoaderCallbacks = new ComicLoaderCallbacks(this);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if(loaderManager.getLoader(COMIC_LOADER_ID) != null){
            loaderManager.initLoader(COMIC_LOADER_ID,null,  comicLoaderCallbacks);
        }
        setup(bundle.getString("email"));
    }

    private void setup(String email){
       /* TextView emailTextView = findViewById(R.id.emailTextView);
        emailTextView.setText(email);
        Button logOut = findViewById(R.id.logOutButton);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                onBackPressed();
            }
        });*/
    }
}