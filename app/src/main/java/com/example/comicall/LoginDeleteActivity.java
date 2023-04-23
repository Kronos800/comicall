package com.example.comicall;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginDeleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button registerbutton = findViewById(R.id.signUpButton);
        Button deletebutton = findViewById(R.id.loginButton);
        deletebutton.setText("Borrar cuenta");
        registerbutton.setVisibility(View.GONE);
        infoDeleteaccount();
        setup();

    }
    private void setup(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                                        currentUser.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Borrar la información del usuario de la base de datos
                                                            //userRef.removeValue();
                                                            db.collection("users").document(email).delete();
                                                            // Regresar a la pantalla de login
                                                            showLogin();

                                                        } else {
                                                            // Mostrar un mensaje de error
                                                           alertDeleteaccount();
                                                        }
                                                    }
                                                });
                                    }else{
                                        alertDeleteaccount();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void alertDeleteError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(("Se ha producido un error al borrar la cuenta"));
        builder.setPositiveButton("Aceptar",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void alertDeleteaccount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cuenta borrada");
        builder.setMessage(("Se ha borrado la cuenta"));
        builder.setPositiveButton("Aceptar",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void infoDeleteaccount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Verificar sus credenciales");
        builder.setMessage(("Introduzca su correo y contraseña para verificar sus datos"));
        builder.setPositiveButton("Aceptar",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void showLogin(){
        Intent loginIntent = new Intent(this,LoginActivity.class);
        startActivity(loginIntent);
        this.finish();
    }
    private void showRegistration(){
        Intent registrationIntent = new Intent(this, RegisterActivity.class);
        startActivity(registrationIntent);
    }
}
