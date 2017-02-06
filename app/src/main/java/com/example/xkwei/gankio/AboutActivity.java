package com.example.xkwei.gankio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by xkwei on 31/01/2017.
 */

public class AboutActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private GestureDetectorCompat mDetector;
    private int mLastX,mLastY;

    private static final String TAG = "AboutActivity";

    public static Intent newIntent(Context context){
        Intent i = new Intent(context,AboutActivity.class);
        return i;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        mToolbar = (Toolbar) findViewById(R.id.about_activity_toolbar);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDetector = new GestureDetectorCompat(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent me){
                Log.i(TAG,"onDown");
                return true;
            }
            @Override
            public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX,float velocityY){

                if(Math.abs(me1.getY()-me2.getY())<200&&velocityX>300&&(me2.getX()-me1.getX())>200) {
                    Log.i(TAG,"on Fling at speedX: " +velocityX +"|speedY: "+velocityY);
                    onBackPressed();
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        mDetector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
}
