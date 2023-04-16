package com.example.comicall;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ComicLoader extends AsyncTask<String, Void, List<Comic>> {

    private static final String MARVEL_BASE_URL = "https://gateway.marvel.com/v1/public/";
    private static final String TS_KEY_HASH = "&ts=1&apikey=f87fcb47ed4e50c7c9736f88c40518ca&hash=6199108e5f83d03422c12931ed4b05eb";
    private static final String COMICS_SEARCH = "comics?dateDescriptor=thisMonth&limit=100";
    private OnTaskCompleted listener;

    public ComicLoader(@NonNull Context context,OnTaskCompleted listener) {

        super();
        this.listener = listener;

    }
    @Override
    protected List<Comic> doInBackground(String... strings) {

            /*Uri builtURI = Uri.parse(MARVEL_BASE_URL).buildUpon()
                    .appendPath(COMICS_SEARCH)
                    .appendPath(TS_KEY_HASH)
                    .build();*/

            String url = MARVEL_BASE_URL+COMICS_SEARCH+TS_KEY_HASH;

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
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ((HomeFragment)listener).setProgressBarVisibility(View.VISIBLE);
    }
    @Override
    protected void onPostExecute(List<Comic> comics) {
        super.onPostExecute(comics);
        ((HomeFragment)listener).setProgressBarVisibility(View.GONE);
        listener.onTaskCompleted(comics);
        listener.onTaskCompleted(comics);
    }

    public interface OnTaskCompleted {
        void onTaskCompleted(List<Comic> comics);
    }
}


