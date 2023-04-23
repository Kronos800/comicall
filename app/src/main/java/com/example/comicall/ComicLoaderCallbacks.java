package com.example.comicall;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.List;

import com.example.comicall.ComicsUpdateListener;

public class ComicLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Comic>>{

    public static final String SEARCH_QUERY = "queryString";
    private Fragment fragment;
    private Context context;
    private ComicsUpdateListener listener;


    public ComicLoaderCallbacks(Fragment searchFragment, ComicsUpdateListener listener){
        this.fragment = searchFragment;
        this.context = searchFragment.getContext();
        this.listener = listener;
    }
    @NonNull
    @Override
    public Loader<List<Comic>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ComicLoader(context, args.getString(SEARCH_QUERY));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Comic>> loader, List<Comic> data) {

        if (listener instanceof HomeFragment) {
            ((HomeFragment) listener).updateComicsResultList((data));
            ((HomeFragment) listener).setProgressBarVisibility(View.GONE);
        } else if (listener instanceof SearchFragment) {
            ((SearchFragment) listener).updateComicsResultList(data);
            ((SearchFragment) listener).setProgressBarVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Comic>> loader) {

    }
}
