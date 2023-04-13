package com.example.comicall;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.List;

public class ComicLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<ComicInfo>> {

    private HomeActivity homeActivity;

    public static final String EXTRA_QUERY = "queryString";
    public static final String EXTRA_PRINT_TYPE = "printType";


    public ComicLoaderCallbacks(HomeActivity homeActivity){ this.homeActivity = homeActivity;}

    @NonNull
    @Override
    public Loader<List<ComicInfo>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ComicLoader(homeActivity,args.getString("queryString"),args.getString("printType"));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<ComicInfo>> loader, List<ComicInfo> data) {
        //homeActivity.updateBooksResultList((data));
        //mainActivity.setProgressBarVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<ComicInfo>> loader) {

    }
}