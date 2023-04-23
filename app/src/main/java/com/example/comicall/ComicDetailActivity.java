package com.example.comicall;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComicDetailActivity extends AppCompatActivity {

    private Comic comic;
    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comic_detail);

        Bundle extras = getIntent().getExtras();

        this.comic = (Comic) extras.getSerializable("comic");

        showCommentsList();

        setup(comic);
    }

    private void setup(Comic comic) {

        String urlImage = comic.getThumbnailUrl().replace("http", "https");
        ImageView imageView = findViewById(R.id.comic_image);

        if (urlImage.contains("image_not_available")) {
            imageView.setImageResource(R.drawable.image_not_found);
        } else {
            Glide.with(this).load(urlImage).into(imageView);
        }

        CollectionReference collection = db.collection("ratings");
        DocumentReference docRef = collection.document(Integer.toString(comic.getId()));

        // COMPRUEBA Y AÑADE LA MEDIA DEL COMIC
        RatingBar ratingAverage = findViewById(R.id.ratingBarAverage);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.contains("avg")) {
                            double avg = document.getDouble("avg");
                            ratingAverage.setRating((float) avg);
                        } else {
                        }
                    } else {
                    }
                }
            }
        });

        TextView title = findViewById(R.id.comic_title);
        title.setText(comic.getTitle());

        TextView series = findViewById(R.id.comic_series);
        series.setText(comic.getSeries());

        TextView writer = findViewById(R.id.comic_writer);
        writer.setText(comic.getWriterName());

        TextView penciler = findViewById(R.id.comic_penciler);
        penciler.setText(comic.getPencilerName());

        TextView description = findViewById(R.id.comic_description);
        description.setText(comic.getDescription());

        //CUANDO EL USUARIO CAMBIE LA VALORACIÓN DEL COMIC
        RatingBar ratings = findViewById(R.id.ratingBar);
        ratings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean b) {

                CollectionReference collection = db.collection("ratings");
                DocumentReference docRef = collection.document(Integer.toString(comic.getId()));

                SharedPreferences preferences = getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE);
                String userEmail = preferences.getString("USER_EMAIL", "");

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            //SI EL DOCUMENTO DENTRO DE RATINGS CON EL ID DE COMIC EXISTE
                            if (document.exists()) {
                                //SI EL COMIC YA TIENE UNA MEDIA
                                if (document.contains("avg") && document.contains("amount") && document.contains("numUsersRated")) {
                                    //AGREGAR NUEVA MEDIA CON EL NUEVO RATE DEL USER
                                    setNewUsersRateCallback(rating);


                                } else {
                                }
                            } else {

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("avg", rating);
                                map.put("amount", rating);
                                map.put("numUsersRated", 1);

                                docRef.set(map);

                                HashMap<String, Object> userRate = new HashMap<>();
                                userRate.put("rate", rating);
                                docRef.collection("userRatings").document(userEmail).set(userRate);


                            }
                        } else {
                            // Error al obtener el documento
                        }
                    }
                });

            }
        });

        FloatingActionButton commentButton = findViewById(R.id.comment_button);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ComicDetailActivity.this);
                builder.setTitle("Añade un comentario");

                // Crea un EditText para que el usuario ingrese su comentario
                final EditText input = new EditText(ComicDetailActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Agrega los botones "Enviar" y "Cancelar" al diálogo
                builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Aquí puedes agregar la lógica para enviar el comentario

                        SharedPreferences preferences = getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE);
                        String userEmail = preferences.getString("USER_EMAIL", "");

                        CollectionReference collection = db.collection("users");
                        DocumentReference usernameDocRef = collection.document(userEmail);


                        usernameDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // SI EXISTE EL DOCUMENTO DEL USUARIO CON ESE EMAIL
                                        String username = document.getString("username");

                                        addCommentCallBack(input.getText().toString(), username);

                                    } else {
                                        //SI NO EXISTE EL DOCUMENTO DEL USUARIO - IMPOSIBLE
                                    }
                                } else {
                                    // Error al obtener el documento
                                }
                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                // Muestra el diálogo
                builder.show();
            }
        });

    }

    private void setNewUsersRateCallback(float rating) {

        CollectionReference collection = db.collection("ratings");
        DocumentReference docRef = collection.document(Integer.toString(comic.getId()));

        SharedPreferences preferences = getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE);
        String userEmail = preferences.getString("USER_EMAIL", "");

        CollectionReference usersCollection = docRef.collection("userRatings");
        DocumentReference userDocRef = usersCollection.document(userEmail);

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // SI EXISTE EL DOCUMENTO DEL USUARIO CON ESE EMAIL Y TIENE RATE
                        if (document.contains("rate")) {
                            double lastRate = document.getDouble("rate");
                            updateAverageUserExistCallback(rating, (float) lastRate);
                        }
                    } else {
                        //SI NO EXISTE EL DOCUMENTO DEL USUARIO CON SU RATE
                        updateAverageUserNotExistCallback(rating);
                    }
                } else {
                    // Error al obtener el documento
                }
            }
        });
    }

    private void updateAverageUserExistCallback(float rating, float lastRate) {
        CollectionReference collection = db.collection("ratings");
        DocumentReference docRef = collection.document(Integer.toString(comic.getId()));

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.contains("amount") && document.contains("numUsersRated") && document.contains("avg")) {
                            Double amount = document.getDouble("amount");
                            Double numUsersRated = document.getDouble("numUsersRated");
                            Double avg = document.getDouble("avg");

                            if (amount != null && numUsersRated != null && avg != null) {
                                amount -= lastRate;
                                amount += rating;
                                avg = amount / (numUsersRated);

                                //ACTUALIZA AMOUNT
                                HashMap<String, Object> amountNuevo = new HashMap<>();
                                amountNuevo.put("amount", amount);

                                docRef.update(amountNuevo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // El documento se actualizó correctamente
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Ocurrió un error al actualizar el documento
                                            }
                                        });

                                //ACTUALIZA AVG
                                HashMap<String, Object> avgNuevo = new HashMap<>();
                                avgNuevo.put("avg", avg);

                                docRef.update(avgNuevo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // El documento se actualizó correctamente
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Ocurrió un error al actualizar el documento
                                            }
                                        });

                                updateUserRate(rating);

                            } else {
                                // El campo "avg" existe pero su valor es nulo
                            }
                        } else {
                            // no existen avg ni
                        }
                    } else {
                    }
                } else {
                    // Error al obtener el documento
                }
            }
        });
    }

    private void updateUserRate(float rating) {
        CollectionReference collection = db.collection("ratings");
        DocumentReference docRef = collection.document(Integer.toString(comic.getId()));

        SharedPreferences preferences = getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE);
        String userEmail = preferences.getString("USER_EMAIL", "");

        CollectionReference usersCollection = docRef.collection("userRatings");
        DocumentReference userDocRef = usersCollection.document(userEmail);

        //ACTUALIZA AMOUNT
        HashMap<String, Object> newRate = new HashMap<>();
        newRate.put("rate", rating);

        userDocRef.update(newRate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al actualizar el documento
                    }
                });

    }

    private void updateAverageUserNotExistCallback(float rating) {

        CollectionReference collection = db.collection("ratings");
        DocumentReference docRef = collection.document(Integer.toString(comic.getId()));

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.contains("amount") && document.contains("numUsersRated") && document.contains("avg")) {
                            Double amount = document.getDouble("amount");
                            Double numUsersRated = document.getDouble("numUsersRated");
                            Double avg = document.getDouble("avg");

                            if (amount != null && numUsersRated != null && avg != null) {
                                amount += rating;
                                numUsersRated += 1;
                                avg = amount / (numUsersRated);

                                //ACTUALIZA AMOUNT
                                HashMap<String, Object> amountNuevo = new HashMap<>();
                                amountNuevo.put("amount", amount);

                                docRef.update(amountNuevo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // El documento se actualizó correctamente
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Ocurrió un error al actualizar el documento
                                            }
                                        });

                                //ACTUALIZA NUMUSERSRATED
                                HashMap<String, Object> numUsersRatedNuevo = new HashMap<>();
                                numUsersRatedNuevo.put("numUsersRated", numUsersRated);

                                docRef.update(numUsersRatedNuevo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // El documento se actualizó correctamente
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Ocurrió un error al actualizar el documento
                                            }
                                        });

                                //ACTUALIZA AVG
                                HashMap<String, Object> avgNuevo = new HashMap<>();
                                avgNuevo.put("avg", avg);

                                docRef.update(avgNuevo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // El documento se actualizó correctamente
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Ocurrió un error al actualizar el documento
                                            }
                                        });

                                updateNotExistingUserRate(rating);

                            } else {
                                // El campo "avg" existe pero su valor es nulo
                            }
                        } else {
                            // no existen avg ni
                        }
                    } else {
                    }
                } else {
                    // Error al obtener el documento
                }
            }
        });
    }

    private void updateNotExistingUserRate(float rating) {
        CollectionReference collection = db.collection("ratings");
        DocumentReference docRef = collection.document(Integer.toString(comic.getId()));


        SharedPreferences preferences = getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE);
        String userEmail = preferences.getString("USER_EMAIL", "");

        HashMap<String, Object> userRate = new HashMap<>();
        userRate.put("rate", rating);
        docRef.collection("userRatings").document(userEmail).set(userRate);

    }

    private void addCommentCallBack(String comment, String username){

        CollectionReference collection = db.collection("comments");
        DocumentReference docRef = collection.document(Integer.toString(comic.getId()));

        CollectionReference usersCollection = docRef.collection("comic_comments");

        Comment newComment = new Comment(username, comment, null);
        usersCollection.add(newComment);


    }

    private void showCommentsList(){
        CollectionReference collection = db.collection("comments");
        DocumentReference docRef = collection.document(Integer.toString(comic.getId()));

        CollectionReference usersCollection = docRef.collection("comic_comments");

        usersCollection.orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Comment> comments = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String username = document.getString("username");
                    String comment = document.getString("comment");
                    Date timestamp = document.getDate("timestamp");
                    Comment comment_for_list = new Comment(username, comment, timestamp);
                    comments.add(comment_for_list);
                }
                commentsAdapter = new CommentsAdapter(this,comments );
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                recyclerView = findViewById(R.id.comments_recycler);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(commentsAdapter);
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}