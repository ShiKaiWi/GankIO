package com.example.xkwei.gankio;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.xkwei.gankio.utils.Constants;
import com.example.xkwei.gankio.widgets.MainFragment;

public class MainActivity extends AppCompatActivity {

    private GestureDetectorCompat mDetector;
    private static final String DEBUG_TAG="MainActivity";
    private ListView mDrawerList;
//    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String[] mCategories;

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
        mCategories = Constants.CATEGORY;
        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_activity_drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.main_activity_drawerLayout_listView);

//        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close){
//
//        };
        mDrawerList.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_list_item,mCategories));

        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position,long id){
                FragmentManager fm = getSupportFragmentManager();
                Fragment fg = createFragment();
                Bundle args = new Bundle();
                args.putInt(MainFragment.CATEGORY,position);
                fg.setArguments(args);
                fm.beginTransaction().replace(R.id.main_fragment_container,fg).commit();
                mDrawerList.setItemChecked(position,true);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });




        mDetector = new GestureDetectorCompat(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDown(MotionEvent me){
                Log.i(DEBUG_TAG,"onDown");
                return false;
            }
            @Override
            public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX,float velocityY){
                Log.i(DEBUG_TAG,"on Fling at speedX: " +velocityX +"|speedY: "+velocityY);
                return false;
            }
        });
    }

//    private class ViewHolder extends
    @Override
    public boolean dispatchTouchEvent(MotionEvent me){

        Log.i(DEBUG_TAG,"on Touch");
        boolean result = mDetector.onTouchEvent(me);

        if(!result){
            super.dispatchTouchEvent(me);
        }
        else{
            Log.i(DEBUG_TAG,"not call super dispatchTouchEvent");
        }
        return true;
    }
}
