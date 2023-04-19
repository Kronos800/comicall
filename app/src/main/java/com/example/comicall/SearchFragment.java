package com.example.comicall;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private ComicLoaderCallbacks comicLoaderCallbacks;
    private ComicAdapter comicAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private List<Comic> comicList = new ArrayList<>();

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        comicLoaderCallbacks = new ComicLoaderCallbacks(this);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if(loaderManager.getLoader(0) != null){
            loaderManager.initLoader(0,null, comicLoaderCallbacks);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(comicAdapter);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        return view;
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtener la vista raíz del Fragment
        View rootView = view.getRootView();

        // Registrar un OnGlobalLayoutListener en la vista raíz
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                SearchView searchView = view.findViewById(R.id.searchView);

                RadioGroup radioGroup = view.findViewById(R.id.SearchRadioGroup);
                int selected = radioGroup.getCheckedRadioButtonId();

                Button searchButton = view.findViewById(R.id.SearchButton);
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setProgressBarVisibility(View.VISIBLE);
                        searchComics(selected, searchView.getQuery().toString());
                    }
                });


                // Remover el OnGlobalLayoutListener después de que se haya ejecutado

            }
        });
    }
    /*
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchView searchView = view.findViewById(R.id.searchView);

        RadioGroup radioGroup = view.findViewById(R.id.SearchRadioGroup);
        int selected = radioGroup.getCheckedRadioButtonId();

        Button searchButton = view.findViewById(R.id.SearchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProgressBarVisibility(View.VISIBLE);
                searchComics(selected, searchView.getQuery().toString());
            }
        });
    }
*/
    public void searchComics (int selected, String searchText){

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(!networkInfo.isConnected() || networkInfo == null){
            return;
        }

        comicAdapter.cleanList();
        comicAdapter.notifyDataSetChanged();


        String searchType = "-1";
        if(selected == R.id.TituloRadioButton){
            searchType = "titulo";
        }else if(selected == R.id.PersonajeRadioButton){
            searchType = "personaje";
        }

        Bundle queryBundle = new Bundle();
        queryBundle.putString(ComicLoaderCallbacks.SEARCH_QUERY, searchText);
        queryBundle.putString(ComicLoaderCallbacks.SEARCH_TYPE, searchType);
        LoaderManager.getInstance(this)
                .restartLoader(0, queryBundle, comicLoaderCallbacks);
    }

    public void updateComicsResultList(List<Comic> comics){

        if(comics == null || comics.isEmpty()){
            return;
        }
        comicAdapter.setComicsData(comics);
        comicAdapter.notifyDataSetChanged();
    }

    public void setProgressBarVisibility(int visibility){
        this.progressBar.setVisibility(visibility);
    }
}