package com.example.comicall.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.comicall.R;
import com.example.comicall.comic.Comic;
import com.example.comicall.comic.ComicAdapter;
import com.example.comicall.comic.ComicLoaderCallbacks;
import com.example.comicall.comic.ComicsUpdateListener;
import com.example.comicall.comic.Creator;
import com.example.comicall.comment.Comment;
import com.example.comicall.comment.CommentsAdapter;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends Fragment implements ComicsUpdateListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ComicLoaderCallbacks comicLoaderCallbacks;
    private RecyclerView recyclerView;
    private ComicAdapter comicAdapter;

    private List<Comic> comicList = new ArrayList<>();

    private ProgressBar progressBar;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SecondFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritesFragment newInstance(String param1, String param2) {
        FavoritesFragment fragment = new FavoritesFragment();
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

        comicAdapter = new ComicAdapter(getContext(), new ArrayList<>());
        comicLoaderCallbacks = new ComicLoaderCallbacks(this, this);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if (loaderManager.getLoader(0) != null) {
            loaderManager.initLoader(0, null, comicLoaderCallbacks);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(comicAdapter);
        progressBar = view.findViewById(R.id.progress_bar);
        this.setProgressBarVisibility(View.VISIBLE);

        searchFavsComics();

        return view;
    }

    public void searchFavsComics() {

        SharedPreferences preferences = getActivity().getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE);
        String userEmail = preferences.getString("USER_EMAIL", "");

        CollectionReference collection = db.collection("favorites");
        DocumentReference favsUserDocRef = collection.document(userEmail);

        CollectionReference comicsFavsCollection = favsUserDocRef.collection("comic_favs");

        comicsFavsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Comic> comics = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {

                    Gson gson = new Gson();

                    List<Creator> creators = new ArrayList<>();
                    ArrayList<HashMap<String,String>> creatorsObject = (ArrayList<HashMap<String,String>>)document.get("creators");

                    for(HashMap<String, String> mapa: creatorsObject){
                        Creator creator = new Creator(mapa.get("name"),mapa.get("role"));
                        creators.add(creator);
                    }

                    List<String> characters = new ArrayList<>();
                    ArrayList<String> charactersObject = (ArrayList<String>)document.get("characters");
                    for(String character: charactersObject){
                        characters.add(character);
                    }

                    List<Comment> comments = new ArrayList<>();
                    ArrayList<HashMap<String,String>> commentsObject = (ArrayList<HashMap<String,String>>)document.get("comments");

                    /*for(HashMap<String, String> mapa: creatorsObject){

                    }*/
                    Long l= new Long((long)document.get("id"));
                    int id=l.intValue();

                    double price = (double)document.get("price");

                    Long l2= new Long((long)document.get("rating"));
                    int rating=l.intValue();

                    Comic comic = new Comic(
                            id,
                            document.get("title").toString(),
                            document.get("description").toString(),
                            document.get("marvel_url").toString(),
                            document.get("series_name").toString(),
                            document.get("saleDate").toString(),
                            (float)price,
                            document.get("image").toString(),
                            creators,
                            characters,
                            rating,
                            comments);

                    comics.add(comic);
                }
                updateComicsResultList(comics);
                setProgressBarVisibility(View.GONE);
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
    }

    @Override
    public void updateComicsResultList(List<Comic> comics) {
        if (comics == null || comics.isEmpty()) {
            return;
        }
        comicAdapter.setComicsData(comics);
        comicAdapter.notifyDataSetChanged();
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        this.progressBar.setVisibility(visibility);
    }
}