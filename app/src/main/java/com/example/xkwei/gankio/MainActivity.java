package com.example.xkwei.gankio;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.example.xkwei.gankio.widgets.MainFragment;

public class MainActivity extends AppCompatActivity {

    private GestureDetectorCompat mDetector;
    private static final String DEBUG_TAG="MainActivity";
    private Fragment createFragment(){
        return MainFragment.getInstance();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fg = fm.findFragmentById(R.id.main_fragment_container);
        if(fg==null){
            fg = createFragment();
            fm.beginTransaction().add(R.id.main_fragment_container,fg).commit();
        }
        mDetector = new GestureDetectorCompat(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent me){
                Log.i(DEBUG_TAG,"onDown");
                return true;
            }
            @Override
            public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX,float velocityY){
                Log.i(DEBUG_TAG,"on Fling originY: "+me1.getY()+"|endY: "+me2.getY());
                return false;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        Log.i(DEBUG_TAG,"on Touch");
        if(mDetector.onTouchEvent(me))
            return super.dispatchTouchEvent(me);
        return false;
    }
}
