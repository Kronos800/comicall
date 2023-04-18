package com.example.comicall;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ComicLoader extends AsyncTask<String, Void, List<Comic>>, AsyncTaskLoader<List<Comic>> implements Serializable {

    private static final String MARVEL_BASE_URL_COMICS = "https://gateway.marvel.com/v1/public/comics?";
    private static final String MARVEL_BASE_URL_CHARACTER = "https://gateway.marvel.com/v1/public/characters?";
    private static final String TIME_STAMP = "ts";

    private static final String TITLE_STAMP = "title";

    private static final String NAME_STAMP = "name";
    private static final String API_KEY = "apikey";

    private static final String HASH_MD5 = "hash";

    private static final String DATE_DESCRIPTOR = "dateDescriptor";
    private static final String LIMIT = "limit";
    private OnTaskCompleted listener;

    private String searchQuery = null;
    private String searchType = null;
    public ComicLoader(@NonNull Context context,OnTaskCompleted listener) {
        super(context);
        this.listener = listener;
    }

    public ComicLoader(@NonNull Context context, String searchQuery, String searchType) {
        super(context);
        this.searchQuery = searchQuery;
        this.searchType = searchType;
    }


    protected List<Comic> doInBackground(String... strings) {

        // Build up the query URI, limiting results to 40 printed books.
        Uri builtURI = Uri.parse(MARVEL_BASE_URL_COMICS).buildUpon()
                .appendQueryParameter(DATE_DESCRIPTOR, "thisMonth")
                .appendQueryParameter(TIME_STAMP, "1")
                .appendQueryParameter(API_KEY, "f87fcb47ed4e50c7c9736f88c40518ca")
                .appendQueryParameter(HASH_MD5, "6199108e5f83d03422c12931ed4b05eb")
                .appendQueryParameter(LIMIT, "100")
                .build();

        String url = builtURI.toString();
        try {
            return downloadURL(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Comic> downloadURL(String myUrl) throws IOException {
        List<Comic> comics = new ArrayList<>();


        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(myUrl);
            conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);

            conn.connect();
            conn.setRequestMethod("GET");
            int response = conn.getResponseCode();

            inputStream = conn.getInputStream();

            Log.d("DEBUG_TAG", "The response is: " + response);
            inputStream = conn.getInputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            in.close();
            return Comic.fromJsonResponse(stringBuilder.toString());

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        } finally {
            // Close the InputStream and connection
            conn.disconnect();
            if (inputStream != null) {
                inputStream.close();
            }

        }
        return comics;
    }

    protected void onPreExecute() {
        //super.onPreExecute();
        ((HomeFragment)listener).setProgressBarVisibility(View.VISIBLE);
    }

    protected void onPostExecute(List<Comic> comics) {
        //super.onPostExecute(comics);
        ((HomeFragment)listener).setProgressBarVisibility(View.GONE);
        listener.onTaskCompleted(comics);
        listener.onTaskCompleted(comics);
    }

    @Nullable
    @Override
    public List<Comic> loadInBackground() {
        try {
            return getComicJson(this.searchQuery, this.searchType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Comic> getComicJson(String searchText, String searchType) throws IOException {
        // Build up the query URI
        Uri builtURI = null;
        if(searchType == "titulo"){
            builtURI = Uri.parse(MARVEL_BASE_URL_COMICS).buildUpon()
                    .appendQueryParameter(TIME_STAMP, "1")
                    .appendQueryParameter(TITLE_STAMP, transformSpaces(searchText))
                    .appendQueryParameter(API_KEY, "f87fcb47ed4e50c7c9736f88c40518ca")
                    .appendQueryParameter(HASH_MD5, "6199108e5f83d03422c12931ed4b05eb")
                    .appendQueryParameter(LIMIT, "100")
                    .build();
        }else if(searchType == "personaje" ){
            builtURI = Uri.parse(MARVEL_BASE_URL_CHARACTER).buildUpon()
                    .appendQueryParameter(TIME_STAMP, "1")
                    .appendQueryParameter(NAME_STAMP, transformSpaces(searchText))
                    .appendQueryParameter(API_KEY, "f87fcb47ed4e50c7c9736f88c40518ca")
                    .appendQueryParameter(HASH_MD5, "6199108e5f83d03422c12931ed4b05eb")
                    .appendQueryParameter(LIMIT, "100")
                    .build();
        }

        String url = builtURI.toString();
        try {
            return downloadURL(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(List<Comic> comics);
    }

    public String transformSpaces (String searchQuery){
        // Comprobar si la cadena contiene espacios
        if (searchQuery.contains(" ")) {
            // Si contiene espacios, reemplazarlos por %20
            return searchQuery.replaceAll(" ", "%20");
        } else {
            // Si no contiene espacios, devolver la cadena original
            return searchQuery;
        }
    }
}


