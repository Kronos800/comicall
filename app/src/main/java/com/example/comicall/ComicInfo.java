package com.example.comicall;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ComicInfo {

    private String title;
    private String authors;
    private URL infoLink;

    public ComicInfo(String title, String authors, URL infoLink ){
        this.title = title;
        this.authors = authors;
        this.infoLink = infoLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public URL getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(URL infoLink) {
        this.infoLink = infoLink;
    }

    public static List<ComicInfo> fromJsonResponse(String info) throws JSONException, MalformedURLException {

        JSONObject data = new JSONObject(info);
        JSONArray ItemsArray = data.getJSONArray("object");

        List<ComicInfo> list = new ArrayList<>();
        for(int i = 0; i < ItemsArray.length(); i++){
            JSONObject book = ItemsArray.getJSONObject(i);
            JSONObject volumeInfo = book.getJSONObject("volumeInfo");
            URL infoLink = new URL(volumeInfo.getString("infoLink"));
            ComicInfo bookInfo = new ComicInfo(volumeInfo.getString("title"), volumeInfo.optString("authors", "Anónimo"), infoLink );
            list.add(bookInfo);
        }
        return list;
    }
}