package com.example.comicall.comic;

import com.example.comicall.comment.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Comic implements Serializable {

    private int id;
    private String title;
    private String description;
    private String marvel_url;
    private String series_name;
    private String saleDate;
    private float price;
    private String image;
    private List<Creator> creators;
    private List<String> characters;

    private int rating;
    private List<Comment> comments;


    public Comic(int id, String title, String description, String marvel_url, String series_name, String saleDate,
                 float price, String image, List<Creator> creators, List<String> characters, int rating, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.marvel_url = marvel_url;
        this.series_name = series_name;
        this.saleDate = saleDate;
        this.price = price;
        this.image = image;
        this.creators = creators;
        this.characters = characters;
        this.rating = rating;
        this.comments = comments;
    }

    public static List<Comic> fromJsonResponse(String info) throws JSONException, MalformedURLException {

        List<Comic> comicList = new ArrayList<>();

        JSONObject json = new JSONObject(info);
        JSONObject data = json.getJSONObject("data");
        JSONArray results = data.getJSONArray("results");

        for (int i = 0; i < results.length(); i++) {

            JSONObject result = results.getJSONObject(i);

            int id = Integer.parseInt(result.getString("id"));
            String title = result.getString("title");
            String description = result.getString("description");

            JSONArray urls = result.getJSONArray("urls");
            JSONObject url = urls.getJSONObject(0);
            String marvel_url = url.getString("url");

            JSONObject serie = result.getJSONObject("series");
            String series_name = serie.getString("name");

            JSONArray dates = result.getJSONArray("dates");
            JSONObject date = dates.getJSONObject(0);
            String saleDate = date.getString("date");

            JSONArray prices = result.getJSONArray("prices");
            JSONObject sale_price = prices.getJSONObject(0);
            float price = Float.parseFloat(sale_price.getString("price"));

            JSONObject thumbnail = result.getJSONObject("thumbnail");
            String image = thumbnail.getString("path");

            JSONObject creators = result.getJSONObject("creators");
            JSONArray items = creators.getJSONArray("items");

            List<Creator> creators_list = new ArrayList<>();

            for (int j = 0; j < items.length(); j++){
                JSONObject creator_json = items.getJSONObject(j);
                Creator creator = new Creator(creator_json.getString("name"),creator_json.getString("role"));
                creators_list.add(creator);
            }

            JSONObject characters = result.getJSONObject("characters");
            JSONArray items_chars = characters.getJSONArray("items");

            List<String> characters_list = new ArrayList<>();

            for (int j = 0; j < items_chars.length(); j++){
                JSONObject character_json = items_chars.getJSONObject(j);
                String character = character_json.getString("name");
                characters_list.add(character);
            }


            URL url_marvel = new URL(marvel_url);

            List<Comment> comments = new ArrayList<>();

            Comic comic = new Comic(id, title, description, url_marvel.toString(), series_name, saleDate,
                    price, image+".jpg", creators_list, characters_list, 5, comments);
            comicList.add(comic);
        }
        return comicList;
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        if(description.equals("null") || description.isEmpty() || description.equals("#N/A"))
            return "No description";
        return description;
    }
    public String getThumbnailUrl(){return image;}

    public String getSeries() { return series_name;
    }

    public String getWriterName() {
        for(Creator creator: creators){
            if(creator.getRole().equals("writer"))
                return creator.getName();
        }
        return "No writer credits";
    }

    public String getPencilerName() {
        for(Creator creator: creators){
            if(creator.getRole().equals("penciler (cover)"))
                return creator.getName();
        }
        return "No penciler credits";
    }
}