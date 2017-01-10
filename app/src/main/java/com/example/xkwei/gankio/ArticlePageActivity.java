package com.example.xkwei.gankio;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.example.xkwei.gankio.widgets.ArticlePageFragment;

/**
 * Created by xkwei on 03/01/2017.
 */

public class ArticlePageActivity extends AppCompatActivity {

    private GestureDetectorCompat mDetector;
    private static final String TAG="ArticlePageActivity";
    private Toolbar mToolbar;

    public static Intent newIntent(Context context,Uri uri){
        Intent i = new Intent(context,ArticlePageActivity.class);
        i.setData(uri);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fg = fm.findFragmentById(R.id.article_view_fragment_container);
        if(fg==null){
            fg = ArticlePageFragment.newInstance(getIntent().getData());
            fm.beginTransaction().add(R.id.article_view_fragment_container,fg).commit();
        }
        mDetector = new GestureDetectorCompat(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent me){
                Log.i(TAG,"onDown");
                return true;
            }
            @Override
            public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX,float velocityY){
                Log.i(TAG,"on Fling at speedX: " +velocityX +"|speedY: "+velocityY);
                if(Math.abs(me1.getY()-me2.getY())<200&&velocityX>300&&(me2.getX()-me1.getX())>200) {
                    onBackPressed();
                    return true;
                }
                return false;
            }
        });

        mToolbar = (Toolbar)findViewById(R.id.activity_article_view_toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){

        Log.i(TAG,"on Touch");
        boolean result = mDetector.onTouchEvent(me);

        if(!result){
            super.dispatchTouchEvent(me);
        }
        else{
            Log.i(TAG,"not call super dispatchTouchEvent");
        }
        return true;
    }


    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        ArticlePageFragment fragment = (ArticlePageFragment) fm.findFragmentById(R.id.article_view_fragment_container);
        WebView wv = fragment.getWebView();
        if(wv.canGoBack()){
            wv.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}
