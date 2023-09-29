package com.example.newsapp;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class NewsDownloaderVolley {
    private static MainActivity mainActivity;
    private static RequestQueue queue;

    private static final String officialURL = "https://newsapi.org/v2/top-headlines";
    private static final String officialSourcesURL = "https://newsapi.org/v2/sources";
    private static final String yourAPIKey = "5501c50761d64f47a7dd9f76db4d5f81";

    public static void downloadNews(MainActivity mainActivityIn) {

        mainActivity = mainActivityIn;

        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(officialSourcesURL).buildUpon();
        buildURL.appendQueryParameter("apiKey", yourAPIKey);
        String urlToUse = buildURL.build().toString();
        Log.d("URL*", urlToUse);

        Response.Listener<JSONObject> listener =
                response -> parseSourcesJSON(response.toString());

        Response.ErrorListener error =
                error1 -> Log.d("error", error1.toString());//mainActivity.updateData(null, null);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "News-App");
                        return headers;
                    }
                };

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void parseSourcesJSON(String s){
        ArrayList<Integer> categoryColors = new ArrayList();
        ArrayList<String> categoryNames = new ArrayList();
        Random rnd = new Random();

        Log.d("RESPONSE*", s);
        try{
            List<Source> sourceObjs = new ArrayList<>();
            JSONObject jObjMain = new JSONObject(s);

            JSONArray sources = jObjMain.getJSONArray("sources");

            for(int i = 0; i < sources.length(); i++){
                JSONObject source = sources.getJSONObject(i);
                String id = source.getString("id");
                String name = source.getString("name");
                String category = source.getString("category");

                if(!categoryNames.contains(category)){
                    int color = Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                    categoryNames.add(category);
                    categoryColors.add(color);
                }


                Source sourceObj = new Source(id, name, category);
                sourceObjs.add(sourceObj);
            }
            mainActivity.updateMenu(categoryNames, sourceObjs, categoryColors);

        }catch (Exception e){

        }
    }

    public static void downloadArticle(MainActivity mainActivityIn, String sourceName) {

        mainActivity = mainActivityIn;

        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(officialURL).buildUpon();
        buildURL.appendQueryParameter("sources", sourceName);
        buildURL.appendQueryParameter("apiKey", yourAPIKey);
        String urlToUse = buildURL.build().toString();
        Log.d("URL*", urlToUse);

        Response.Listener<JSONObject> listener =
                response -> parseJSON(response.toString());

        Response.ErrorListener error =
                error1 -> Log.d("error", error1.toString());

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "News-App");
                        return headers;
                    }
                };

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private static void parseJSON(String s){
        Log.d("Article Data", s);
        try{
            List<Article> articleObjs = new ArrayList<>();
            JSONObject jObjMain = new JSONObject(s);

            if(jObjMain.has("articles")){
                JSONArray articles = jObjMain.getJSONArray("articles");

                for(int i = 0; i < articles.length(); i++){
                    JSONObject article = articles.getJSONObject(i);
                    String author = "";
                    String description = "";
                    String title = "";
                    String publishedAt = "";
                    String urlToImage = "";
                    String url = "";

                    if(article.has("author"))
                        author = article.getString("author");
                    if(article.has("description"))
                        description = article.getString("description");
                    if(article.has("title"))
                        title = article.getString("title");
                    if(article.has("urlToImage"))
                        urlToImage = article.getString("urlToImage");
                    if(article.has("url"))
                        url = article.getString("url");
                    if(article.has("publishedAt"))
                        publishedAt = article.getString("publishedAt");

                    Article articleObj = new Article(author, title, description, url, urlToImage, publishedAt);

                    articleObjs.add(articleObj);
                }
                mainActivity.updateNewsArticles(articleObjs);
            }

        }catch (Exception e){
            Log.d("Error message:", e.getMessage());
        }
    }
}