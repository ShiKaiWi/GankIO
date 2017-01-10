package com.example.xkwei.gankio.utils;

import android.net.Uri;
import android.renderscript.Double2;
import android.support.v7.app.ActionBarDrawerToggle;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by xkwei on 01/01/2017.
 */

public class GankIOAPI {

    private static final String TAG = "GankIOAPI";
    /***
            "_id": "586528c7421aa94dbe2ccdae",
            "createdAt": "2016-12-29T23:16:23.876Z",
            "desc": "H5\u5524\u8d77\u539f\u751f\u5e94\u7528",
            "publishedAt": "2016-12-30T16:16:11.125Z",
            "source": "web",
            "type": "Android",
            "url": "http://ihongqiqu.com/2015/12/03/html-call-native-app/",
            "used": true,
            "who": "Jin"
    ***/
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'", Locale.CHINA);
    public static final SimpleDateFormat DATE_FORMATTER2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S",Locale.CHINA);
    public static Date parseDate(String dateString){
        if(null==dateString){
            Log.i(TAG,"missing the date");
            return null;
        }
        Date date = null;
        try{
            date = DATE_FORMATTER.parse(dateString);
        }catch(ParseException pe){
            Log.i(TAG,"cannot parse the date string "+dateString +" trying to use the Formatter2");
            try{
                date = DATE_FORMATTER2.parse(dateString);
            }catch(ParseException pe2){
                Log.i(TAG,"still cannot parse the date string");
            }
        }
        return date;
    }

    /***
     * http://gank.io/api/search/query/listview/category/Android/count/10/page/1
     * @param query
     * @param pageNum
     * @return
     */
    public static RealmList<Article> getQueryResult(String query,int pageNum){
        Uri uri = null;
        String url = null;
        RealmList<Article> articles = null;
            uri = new Uri.Builder().scheme("http")
                    .authority(Constants.BASE_URL)
                    .appendPath(Constants.GANK_API)
                    .appendPath(Constants.SEARCH)
                    .appendPath(Constants.QUERY)
                    .appendPath(query)
                    .appendPath(Constants.GANK_PATH_CATEGORY)
                    .appendPath(Constants.ALL)
                    .appendPath(Constants.GANK_PATH_COUNT)
                    .appendPath("10")
                    .appendPath(Constants.GANK_PATH_PAGE)
                    .appendPath(Integer.toString(pageNum))
                    .build();
            JSONObject job = getUriJSONObject(uri);
            articles = parseJSONObject(job,true);
        return articles;
    }
    public static RealmList<Article> getData(String type,int pageNum){
        Uri uri = null;
        String url = null;
        RealmList<Article> articles = null;
        for(String definedTypes:Constants.CATEGORY){
            if(type.equals(definedTypes))
                continue;
            uri = new Uri.Builder().scheme("http")
                    .authority(Constants.BASE_URL)
                    .appendPath(Constants.GANK_API)
                    .appendPath(Constants.GANK_DATA)
                    .appendPath(type)
                    .appendPath("10")
                    .appendPath(Integer.toString(pageNum))
                    .build();
            JSONObject job = getUriJSONObject(uri);
            articles = parseJSONObject(job,false);
        }
        return articles;
    }

    private static RealmList<Article> parseJSONObject(JSONObject job,boolean isQuery){
        RealmList<Article> articles = new RealmList<>();

        try {
            JSONArray results = job.getJSONArray(Constants.JSON_RESULTS);
            JSONObject result;
            for(int i=0;i<results.length();i++){
                result = results.getJSONObject(i);
                Article article = new Article(result.getString(isQuery?Constants.JSON_RESULT_ID_GANHUO:Constants.JSON_RESULT_ID));
                article.setDescription(result.getString(Constants.JSON_RESULT_DESCRITION));
                article.setPublishDate(result.getString(Constants.JSON_REUSLT_PUBLISH_DATE));
                article.setType(result.getString(Constants.JSON_RESULT_TYPE));
                article.setUrl(result.getString(Constants.JSON_RESULT_URL));
                article.setAuthor(result.getString(Constants.JSON_RESULT_AUTHOR));

                if(!isQuery) {
                    article.setCreateDate(result.getString(Constants.JSON_RESULT_CREATE_DATE));
                    article.setTitle();
                }
                article.setSearchingResult(isQuery);
                article.setDate(parseDate(article.getPublishDate()));
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
