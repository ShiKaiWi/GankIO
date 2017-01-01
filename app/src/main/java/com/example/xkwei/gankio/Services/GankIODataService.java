package com.example.xkwei.gankio.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.xkwei.gankio.models.Article;
import com.example.xkwei.gankio.utils.Constants;
import com.example.xkwei.gankio.utils.GankIOAPI;

import java.util.List;

/**
 * Created by xkwei on 01/01/2017.
 */

public class GankIODataService extends IntentService {
    private static final String TAG="GankIODataService";
    @Override
    public void onHandleIntent(Intent intent){
        String type = intent.getStringExtra(Constants.ARTICLE_TYPE);
        List<Article> articles = GankIOAPI.getData(type);
        Log.i(TAG,"have finished the request for "+type+" articls");
    }

    public static Intent newIntentWithType(Context context,String type){
        Intent i = new Intent(context,GankIODataService.class);
        i.putExtra(Constants.ARTICLE_TYPE,type);
        return i;
    }
    public GankIODataService(){
        super(TAG);
    }
}
