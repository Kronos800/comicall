package com.example.comicall.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comicall.usersManagment.LoginActivity;
import com.example.comicall.usersManagment.LoginDeleteActivity;
import com.example.comicall.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView imagenPerfil;
    private TextView nombreUsuario;
    private TextView correoUsuario;

    private Button guardarbutton;
    private Button cerrarSesionButton;

    private Button borrarCuentaButton;

    private FirebaseAuth mAuth;

    FirebaseUser currentUser;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setup(view);
        return view;
    }

    private void setup(View view){


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        guardarbutton = view.findViewById(R.id.saveButton);
        imagenPerfil = view.findViewById(R.id.profile_image);
        nombreUsuario = view.findViewById(R.id.profile_username);
        correoUsuario = view.findViewById(R.id.profile_email);
        cerrarSesionButton = view.findViewById(R.id.signoutbutton);
        borrarCuentaButton = view.findViewById(R.id.deleteAccountButton);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String correoUsuariokey = currentUser.getEmail();
        DocumentReference userRefd = db.collection("users").document(correoUsuariokey);
        userRefd.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String nombreUsuariostring = document.getString("username"); // Reemplaza con el nombre del campo que contiene el nombre de usuario
                    nombreUsuario.setText(nombreUsuariostring);
                }
            }
        });
        correoUsuario.setText(currentUser.getEmail());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users/" + currentUser.getUid());
        String email = currentUser.getEmail();

        // Actualizar el nombre de usuario en la base de datos al presionar el botón de guardar
        guardarbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hashmap clave valor de los datos a introducir
                HashMap<String,String> username = new HashMap<>();

                //añade al hashmap
                username.put("username",nombreUsuario.getText().toString());
                // si no existe la coleccion la crea y si existe añade los valores del set
                // Tambien sirve para actualizar datos

                db.collection("users").document(currentUser.getEmail()).set(username);
                Toast.makeText(getActivity(), "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show();

            }
        });

        // Botón para borrar la cuenta del usuario
        borrarCuentaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar un diálogo de confirmación
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("¿Está seguro de que desea borrar su cuenta?");
                builder.setMessage("Esta acción no se puede deshacer y se perderán todos los datos asociados con su cuenta.");
                builder.setPositiveButton("Sí, borrar mi cuenta", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Borrar la cuenta del usuario
                        String email = currentUser.getEmail();

                        Intent intent = new Intent(getActivity(), LoginDeleteActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancelar", null);
                builder.show();
            }
        });


        // Botón para cerrar sesión
        cerrarSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                alertLogOut();
                Intent anteriorIntent = getActivity().getIntent();
                Bundle bundle = anteriorIntent.getExtras();
                showLogin(bundle.getString("email"));
            }
        });
    }
    private void alertLogOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sesion cerrada");
        builder.setMessage(("Se ha cerrado la sesion"));
        builder.setPositiveButton("Aceptar",null);
        Dialog dialog = builder.create();
        dialog.show();
    }
    private void showLogin (String email){
        Intent homeIntent = new Intent(getContext(), LoginActivity.class);
        homeIntent.putExtra("email",email);
        startActivity(homeIntent);
    }
}