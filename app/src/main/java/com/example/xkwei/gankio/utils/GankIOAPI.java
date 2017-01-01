package com.example.xkwei.gankio.utils;

import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.xkwei.gankio.models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xkwei on 01/01/2017.
 */

public class GankIOAPI {

    private static final String TAG = "GankIOAPI";
    public static List<Article> getData(String type){
        Uri uri = null;
        String url = null;
        List<Article> articles = null;
        if(type.equals(Constants.ANDROID)){
            uri = new Uri.Builder().scheme("http")
                    .authority(Constants.BASE_URL)
                    .appendPath(Constants.GANK_API)
                    .appendPath(Constants.GANK_DATA)
                    .appendPath(Constants.ANDROID)
                    .appendPath("10")
                    .appendPath("1")
                    .build();
            JSONObject job = getUriJSONObject(uri);
            articles = parseJSONObject(job);
        }
        return articles;
    }

    private static List<Article> parseJSONObject(JSONObject job){
        List<Article> articles = new ArrayList<>();

        try {
            JSONArray results = job.getJSONArray(Constants.JSON_RESULTS);
            JSONObject result;
            for(int i=0;i<results.length();i++){
                result = results.getJSONObject(i);
                Article article = new Article(result.getString(Constants.JSON_RESULT_ID));
                article.setAuthor(result.getString(Constants.JSON_RESULT_AUTHOR));
                article.setCreateDate(result.getString(Constants.JSON_RESULT_CREATE_DATE));
                article.setDescription(result.getString(Constants.JSON_RESULT_DESCRITION));
                article.setUrl(result.getString(Constants.JSON_RESULT_URL));
                article.setType(result.getString(Constants.JSON_RESULT_TYPE));
                articles.add(article);
                Log.i(TAG,"got the Article "+article.getUrl());
            }
        }catch(JSONException je){
            je.printStackTrace();
        }

        return articles;
    }

    private static JSONObject getUriJSONObject(Uri uri){
        JSONObject job = null;
        try{
            job = new JSONObject(new String(getUriBytes(uri)));
        }catch(IOException e){
            e.printStackTrace();
        }catch(JSONException je){
            je.printStackTrace();
        }finally{
            return job;
        }
    }
    private static byte[] getUriBytes(Uri uri) throws IOException{
        URL url;
        HttpURLConnection con = null;
        ByteArrayOutputStream out = null;
        try{
            url = new URL(uri.toString());
            con = (HttpURLConnection)url.openConnection();
            InputStream in = con.getInputStream();
            out = new ByteArrayOutputStream();
            if(con.getResponseCode()!=HttpURLConnection.HTTP_OK){
                throw new IOException(con.getResponseMessage()+" from "+url.toString());
            }
            int bytesRead = 0;
            byte[] bytesBuffer = new byte[1024];
            while((bytesRead=in.read(bytesBuffer))>0){
                out.write(bytesBuffer,0,bytesRead);
            }
            in.close();
            out.close();
        }catch(MalformedURLException e){

        }catch(IOException e){

        }finally{
            if(con!=null)
                con.disconnect();
            if(out!=null){
                return out.toByteArray();
            }else
                return null;
        }

    }
}
