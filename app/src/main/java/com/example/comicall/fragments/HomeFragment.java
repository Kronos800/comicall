package com.example.comicall.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.example.comicall.R;
import com.example.comicall.comic.Comic;
import com.example.comicall.comic.ComicAdapter;
import com.example.comicall.comic.ComicLoaderCallbacks;
import com.example.comicall.comic.ComicsUpdateListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ComicsUpdateListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ComicLoaderCallbacks comicLoaderCallbacks;
    private RecyclerView recyclerView;
    private ComicAdapter comicAdapter;

    private List<Comic> comicList = new ArrayList<>();

    private ProgressBar progressBar;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirstFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        comicLoaderCallbacks = new ComicLoaderCallbacks(this,this);
        LoaderManager loaderManager = LoaderManager.getInstance(this);
        if(loaderManager.getLoader(0) != null){
            loaderManager.initLoader(0,null, comicLoaderCallbacks);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(comicAdapter);
        progressBar = view.findViewById(R.id.progress_bar);
        this.setProgressBarVisibility(View.VISIBLE);

        searchComics("");

        return view;
    }




    public void searchComics (String searchText){

        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(!networkInfo.isConnected() || networkInfo == null){
            return;
        }

        comicAdapter.cleanList();
        comicAdapter.notifyDataSetChanged();


        Bundle queryBundle = new Bundle();
        queryBundle.putString(ComicLoaderCallbacks.SEARCH_QUERY, searchText);
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