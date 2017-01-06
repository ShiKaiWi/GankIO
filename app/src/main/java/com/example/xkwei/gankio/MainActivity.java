package com.example.xkwei.gankio;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private int lastCategoryIndex;
    private Fragment createFragment(int CategoryIndex){

        return MainFragment.getInstance(CategoryIndex);
    }
    private Fragment createFragment(){
        return createFragment(0);
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
        lastCategoryIndex = 0;
        mCategories = Constants.CATEGORY;
        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_activity_drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.main_activity_drawerLayout_listView);

        mDrawerList.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_list_item,mCategories));

        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position,long id){
                if(position!=lastCategoryIndex) {
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment fg = createFragment(position);
                    fm.beginTransaction().replace(R.id.main_fragment_container, fg).commitNow();
                    lastCategoryIndex = position;
                }
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        mDrawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onPause(){
        super.onPause();
        mDrawerLayout.setDrawerLockMode( DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
}
