package com.example.comicall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setup();

    }
    private void setup(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.putExtra("email", currentUser.getEmail());
            startActivity(homeIntent);
            finish();
        }
        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegistration();
            }
        });
        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputLayout emailEdit = findViewById(R.id.emailEditText);
                String email = emailEdit.getEditText().getText().toString();

                TextInputLayout passwordEdit = findViewById(R.id.passwordEditText);
                String password = passwordEdit.getEditText().getText().toString();

                if(!email.isEmpty() && !password.isEmpty()){
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        showHome(task.getResult().getUser().getEmail());
                                    }else{
                                        alertLogin();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void alertLogin(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(("Se ha producido un error durante el acceso"));
        builder.setPositiveButton("Aceptar",null);
        Dialog dialog = builder.create();
        dialog.show();
    }
    private void showHome(String email){
        Intent homeIntent = new Intent(this,HomeActivity.class);
        homeIntent.putExtra("email",email);
        startActivity(homeIntent);
    }
    private void showRegistration(){
        Intent registrationIntent = new Intent(this, RegisterActivity.class);
        startActivity(registrationIntent);
    }
}
