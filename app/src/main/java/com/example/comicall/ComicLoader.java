package com.example.comicall;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ComicLoader extends AsyncTaskLoader<List<ComicInfo>> {

    private final String queryString;
    private final String printType;
    public ComicLoader(@NonNull Context context, String queryString, String printType) {
        super(context);
        this.queryString = queryString;
        this.printType = printType;
    }

    @Nullable
    @Override
    public List<ComicInfo> loadInBackground() {
        try {
            return getBookInfoJson(this.queryString, this.printType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onStartLoading(){
        forceLoad();
    }

    public List<ComicInfo> getBookInfoJson(String queryString, String printType) throws IOException {
        // Base URL for the Books API.
        final String MARVEL_BASE_URL = "https://gateway.marvel.com/v1/public/";
        final String API_KEY = "&ts=1&apikey=f87fcb47ed4e50c7c9736f88c40518ca&hash=6199108e5f83d03422c12931ed4b05eb";

        // Parameter for the search string
        final String COMICS_PARAM = "comics?";

        // Build up the query URI, limiting results to 40 printed books.
        String myurl = MARVEL_BASE_URL + COMICS_PARAM + API_KEY;
        return downloadUrl(myurl);
    }

    private List<ComicInfo> downloadUrl(String myurl) throws IOException {

        List<ComicInfo> list = new ArrayList<ComicInfo>();

        InputStream inputStream = null;


        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            // Start the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("DEBUG_TAG", "The response is: " + response);
            inputStream = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = convertInputToString(inputStream);
            Log.d("DEBUG_TAG","La respuesta es: " + contentAsString);


            return ComicInfo.fromJsonResponse(contentAsString);

        }catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }finally {
            // Close the InputStream and connection
            conn.disconnect();
            if (inputStream != null) {
                inputStream.close();
            }

        }
        return list;
    }

    // Reads an InputStream and converts it to a String.
    public String convertInputToString(InputStream stream)
            throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line).append("\n");
        }

        bufferedReader.close();

        return stringBuilder.toString();
    }

}