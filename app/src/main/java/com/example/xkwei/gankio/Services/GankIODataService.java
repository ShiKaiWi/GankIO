package com.example.xkwei.gankio.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ActivityUnitTestCase;
import android.util.Log;

import com.example.xkwei.gankio.models.Article;
import com.example.xkwei.gankio.utils.Constants;
import com.example.xkwei.gankio.utils.GankIOAPI;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;

/**
 * Created by xkwei on 01/01/2017.
 */

public class GankIODataService extends IntentService {
    private static final String TAG="GankIODataService";
    public static final String ACTION_UPDATE_DATA="GankIOService_Update_Data";
    public static final String ACTION_QUERY ="GankIOService_Query";
    private LocalBroadcastManager mLocalBroadcastManager;
    private Realm mRealm;

    @Override
    public void onCreate(){
        super.onCreate();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this.getApplicationContext());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return START_STICKY;
//    }

    @Override
    public void onHandleIntent(Intent intent){
        if(intent==null){
            Log.i(TAG,"got a null intent");
            return;
        }
        mRealm = Realm.getDefaultInstance();
        RealmList<Article> articles;
        int pageNum = intent.getIntExtra(Constants.PAGE_NUM,1);
        String type = intent.getStringExtra(Constants.ARTICLE_TYPE);
        if(null!=type) {
            articles = GankIOAPI.getData(type, pageNum);
            RealmQuery<Article> mRealmQeury = mRealm.where(Article.class);
            for(Article article:articles){
                if(null==mRealmQeury.equalTo("mId",article.getId()).findFirst()){
                    mRealm.beginTransaction();
                    mRealm.copyToRealm(article);
                    mRealm.commitTransaction();
                }
                else
                    break;
            }
            Log.i(TAG, "have finished the request for " + type + " articles");
            sendResult(ACTION_UPDATE_DATA);
            return;
        }
        String query = intent.getStringExtra(Constants.QUERY);
        if(null!=query){
            articles = GankIOAPI.getQueryResult(query,pageNum);
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(articles);
            mRealm.commitTransaction();
            Log.i(TAG,"have finished the query request");
            sendResult(ACTION_QUERY);
            return;
        }
    }

    private void sendResult(String resultType){
        Intent i = new Intent(resultType);
        mLocalBroadcastManager.sendBroadcast(i);
    }

    public static Intent newIntentWithTypeAndPage(Context context,String type,int pageNumber){
        Intent i = new Intent(context,GankIODataService.class);
        i.putExtra(Constants.ARTICLE_TYPE,type);
        i.putExtra(Constants.PAGE_NUM,pageNumber);
        return i;
    }
    public static Intent newIntentForSearch(Context context,String query){
        return newIntentForSearchWithPage(context,query,1);
    }

    public static Intent newIntentForSearchWithPage(Context context,String query,int pageNumber){
        Intent i = new Intent(context,GankIODataService.class);
        i.putExtra(Constants.QUERY,query);
        i.putExtra(Constants.PAGE_NUM,pageNumber);
        return i;
    }
    public GankIODataService(){
        super(TAG);
    }
}
