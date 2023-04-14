package com.example.comicall;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity  extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setup();
    }
    private void setup(){
        TextInputLayout usernameTextInputLayout = findViewById(R.id.usernameEditText);
        EditText usernameEditText = findViewById(R.id.username_edit_text);
        Button signUpButton = findViewById(R.id.signUpButton);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se utiliza
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No se utiliza
            }
            @Override
            public void afterTextChanged(Editable s) {
                String username = s.toString().trim();

                if (isValidUsername(username)) {
                    usernameTextInputLayout.setError(null);
                    signUpButton.setEnabled(true);
                } else {
                    usernameTextInputLayout.setError("Username inválido");
                    signUpButton.setEnabled(false);
                }
            }
        });

        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextInputLayout emailEdit = findViewById(R.id.emailEditText);
                emailEdit.setHintAnimationEnabled(false);
                String email = emailEdit.getEditText().getText().toString();

                TextInputLayout passwordEdit = findViewById(R.id.passwordEditText);
                String password = passwordEdit.getEditText().getText().toString();

                // hashmap clave valor de los datos a introducir
                HashMap<String,String> username = new HashMap<>();

                TextInputLayout usernameText = findViewById(R.id.usernameEditText);

                //añade al hashmap
                username.put("username",usernameText.getEditText().getText().toString());
                // si no existe la coleccion la crea y si existe añade los valores del set
                // Tambien sirve para actualizar datos
                db.collection("users").document(email).set(username);


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
    }
    private void alertSignUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(("Se ha producido un error durante el registro del usuario"));
        builder.setPositiveButton("Aceptar",null);
        Dialog dialog = builder.create();
        dialog.show();
    }
    private void showHome(String email){
        Intent homeIntent = new Intent(this,HomeActivity.class);
        homeIntent.putExtra("email",email);
        startActivity(homeIntent);
    }
    private boolean isValidUsername(String username){
        return username.matches("^[a-zA-Z0-9]+$");
    }
}
