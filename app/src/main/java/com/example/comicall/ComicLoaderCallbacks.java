package com.example.comicall;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.List;

public class ComicLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Comic>>{

    public static final String SEARCH_QUERY = "queryString";
    public static final String SEARCH_TYPE = "printType";
    private SearchFragment searchFragment;
    private Context context;

    public ComicLoaderCallbacks(SearchFragment searchFragment){
        this.searchFragment = searchFragment;
        this.context = searchFragment.getContext();
    }
    @NonNull
    @Override
    public Loader<List<Comic>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ComicLoader(searchFragment.requireContext(), args.getString(SEARCH_QUERY), args.getString(SEARCH_TYPE));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Comic>> loader, List<Comic> data) {
        searchFragment.updateComicsResultList((data));
        searchFragment.setProgressBarVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Comic>> loader) {

    }
}
