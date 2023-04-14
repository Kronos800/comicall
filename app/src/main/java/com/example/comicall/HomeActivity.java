package com.example.comicall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private ComicLoaderCallbacks comicLoaderCallbacks;
    private static final int COMIC_LOADER_ID = 0;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent anteriorIntent = getIntent();
        Bundle bundle = anteriorIntent.getExtras();

        setup(bundle.getString("email"));
    }

    private void setup(String email){
        Button guardar = findViewById(R.id.saveButton);
        HashMap<String,String> username = new HashMap<>();
        EditText usernameText = findViewById(R.id.usernameEditName);
        username.put("username",usernameText.getText().toString());
        db.collection("users").document(email).set(username);
    }
}