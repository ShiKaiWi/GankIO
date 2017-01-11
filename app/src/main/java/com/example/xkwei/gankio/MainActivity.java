package com.example.xkwei.gankio;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.xkwei.gankio.services.GankIODataService;
import com.example.xkwei.gankio.utils.Constants;
import com.example.xkwei.gankio.widgets.MainFragment;
import com.example.xkwei.gankio.widgets.SearchFragment;

public class MainActivity extends AppCompatActivity {

//    private GestureDetectorCompat mDetector;
    private static final String DEBUG_TAG="MainActivity";
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private int[] mCategoryId;
    private int currentCategoryIndex;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private boolean mIsSearching;
    private String currentQuery;
    private Fragment createFragment(int CategoryIndex){

        return MainFragment.getInstance(CategoryIndex);
    }
    private Fragment createFragment(){
        return createFragment(0);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIsSearching = false;

        FragmentManager fm = getSupportFragmentManager();
        Fragment fg = fm.findFragmentById(R.id.main_fragment_container);
        if(fg==null){
            fg = createFragment();
            fm.beginTransaction().add(R.id.main_fragment_container,fg).commit();
        }
        fg = fm.findFragmentById(R.id.search_fragment_container);
        if(null==fg){
            fg = SearchFragment.getInstance();
            fm.beginTransaction().add(R.id.search_fragment_container,fg).commit();
        }

        currentCategoryIndex = 0;
        mCategoryId = Constants.CATEGORY_ID;
        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_activity_drawerLayout);
        mNavigationView = (NavigationView)findViewById(R.id.main_activity_drawerLayout_navigation_bar);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem mit){
                int id = mit.getItemId();
                for(int i=0;i<mCategoryId.length;i++) {
                    if(id==mCategoryId[i]&&i!= currentCategoryIndex){
                        FragmentManager fm = getSupportFragmentManager();
                        Fragment fg = createFragment(i);
                        fm.beginTransaction().replace(R.id.main_fragment_container, fg).commitNow();
                        mToolbar.setTitle(Constants.CATEGORY[i]);
                        currentCategoryIndex = i;
                    }
                }
                if(mIsSearching){
                    mIsSearching = false;
                    updateVisibilityOfFragments();
                }
                mNavigationView.setCheckedItem(id);
                mDrawerLayout.closeDrawer(mNavigationView);
                invalidateOptionsMenu();
                return true;
            }
        });




/***
 * deprecate using the ListView as navigationBar
 */

//        mDrawerList = (ListView) findViewById(R.id.main_activity_drawerLayout_listView);
//
//
//        mDrawerList.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_list_item,mCategories));
//
//        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,int position,long id){
//                if(position!=currentCategoryIndex) {
//                    FragmentManager fm = getSupportFragmentManager();
//                    Fragment fg = createFragment(position);
//                    fm.beginTransaction().replace(R.id.main_fragment_container, fg).commitNow();
//                    currentCategoryIndex = position;
//                }
//                mDrawerList.setItemChecked(position, true);
//                mDrawerLayout.closeDrawer(mDrawerList);
//            }
//        });

        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        mToolbar.setTitle(Constants.ANDROID);

        if(Build.VERSION.SDK_INT>22)
            mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white,getTheme()));
        else
            mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        setSupportActionBar(mToolbar);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View view){
                super.onDrawerOpened(view);
                getSupportActionBar().setTitle(R.string.app_name);
            }
        };

//        mToolbar.setVisibility(View.GONE);
        //mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        //mToolbar.setNavigationIcon(R.drawable.ic_drawer);
//        mToolbar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        );
//        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
//        mActionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater mif = getMenuInflater();
        mif.inflate(R.menu.activity_main_menu,menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setIconifiedByDefault(true);
//        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                mToolbar.setTitle("Articles about \"" + currentQuery+"\"");
//                return false;
//            }
//        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mIsSearching = true;
                currentQuery = query;
                updateVisibilityOfFragments();
                FragmentManager fm = getSupportFragmentManager();
                Fragment fg = fm.findFragmentById(R.id.search_fragment_container);
                if(null==fg){
                    fg = SearchFragment.getInstance(query);
                    fm.beginTransaction().add(R.id.search_fragment_container,fg).commit();
                }else{
                    ((SearchFragment)fg).setQuery(query);
                }
//                searchItem.collapseActionView();
                invalidateOptionsMenu();
                Intent i = GankIODataService.newIntentForSearch(MainActivity.this,query);
                startService(i);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    private void updateVisibilityOfFragments(){
        FrameLayout mainFragmentContainer = (FrameLayout)findViewById(R.id.main_fragment_container);
        FrameLayout searchFragmentContainer =(FrameLayout)findViewById(R.id.search_fragment_container);
        if(mIsSearching){
            searchFragmentContainer.setVisibility(View.VISIBLE);
            mainFragmentContainer.setVisibility(View.INVISIBLE);
            mToolbar.setTitle("\"" + currentQuery + "\"");
        }
        else{
            searchFragmentContainer.setVisibility(View.INVISIBLE);
            mainFragmentContainer.setVisibility(View.VISIBLE);
            mToolbar.setTitle(Constants.CATEGORY[currentCategoryIndex]);
        }
//        FrameLayout mainFrameLayOut = (FrameLayout) findViewById(R.id.main_activity_frame_layout);
//        mainFrameLayOut.invalidate();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
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

    @Override
    public void onBackPressed(){
        if(mIsSearching){
            mIsSearching = false;
            updateVisibilityOfFragments();
            return;
        }
        super.onBackPressed();
    }
}
