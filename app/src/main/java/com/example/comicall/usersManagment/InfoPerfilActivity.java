package com.example.comicall.usersManagment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comicall.R;
import com.google.firebase.auth.FirebaseAuth;

public class InfoPerfilActivity extends AppCompatActivity {

    private ImageView imagenPerfil;
    private TextView nombreUsuario;
    private TextView correoUsuario;

    private Button cerrarSesionButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_perfil);

        mAuth = FirebaseAuth.getInstance();
        imagenPerfil = findViewById(R.id.profile_image);
        nombreUsuario = findViewById(R.id.profile_username);
        correoUsuario = findViewById(R.id.profile_email);
        cerrarSesionButton = findViewById(R.id.signoutbutton);


        //mirar como sacar de la base de datos la imagen, nombre y correo

        imagenPerfil.setImageResource(R.drawable.icono_logo_comicall);//CAMBIAR A LOS DATOS DE LA BASE DE DATOS
        nombreUsuario.setText("Michael Paredes Sanchez");
        correoUsuario.setText("michael.paredes@gmail.com");

        cerrarSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(InfoPerfilActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

