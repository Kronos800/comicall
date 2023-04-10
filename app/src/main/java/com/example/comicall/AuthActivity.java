package com.example.comicall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setup();

    }
    private void setup(){
        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEdit = findViewById(R.id.emailEditText);
                String email = emailEdit.getText().toString();

                EditText passwordEdit = findViewById(R.id.passwordEditText);
                String password = passwordEdit.getText().toString();

                if(!email.isEmpty() && !password.isEmpty()){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        showHome(task.getResult().getUser().getEmail());
                                    }else{
                                        alertSignUp();
                                    }
                                }
                            });
                }
            }
        });
        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEdit = findViewById(R.id.emailEditText);
                String email = emailEdit.getText().toString();

                EditText passwordEdit = findViewById(R.id.passwordEditText);
                String password = passwordEdit.getText().toString();

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
    private void alertSignUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(("Se ha producido un error durante el registro del usuario"));
        builder.setPositiveButton("Aceptar",null);
        Dialog dialog = builder.create();
        dialog.show();
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
}
