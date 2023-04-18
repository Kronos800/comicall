package com.example.comicall;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if(loaderManager.getLoader(0) != null){
            loaderManager.initLoader(0,null, comicLoaderCallbacks);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // El texto ingresado se envía al método searchComics
                searchComics(view,query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // No se hace nada aquí
                return false;
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(comicAdapter);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        return view;
    }

    public void searchComics (View view, String searchText){
        this.setProgressBarVisibility(View.VISIBLE);
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(!networkInfo.isConnected() || networkInfo == null){
            return;
        }

        comicAdapter.cleanList();
        comicAdapter.notifyDataSetChanged();


        RadioGroup radioGroup = view.findViewById(R.id.SearchRadioGroup);
        int selected = radioGroup.getCheckedRadioButtonId();


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