package com.example.xkwei.gankio;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
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
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.xkwei.gankio.bases.BaseFragment;
import com.example.xkwei.gankio.contents.SearchSuggestionProvider;
import com.example.xkwei.gankio.utils.Constants;
import com.example.xkwei.gankio.widgets.CollectionFragment;
import com.example.xkwei.gankio.widgets.MainFragment;
import com.example.xkwei.gankio.widgets.SearchFragment;

public class MainActivity extends AppCompatActivity {

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
    private Handler mHandler = new Handler();
    private Fragment[] mMainFragments = new MainFragment[MainFragment.CATEGORY_NUM];
    private Fragment mSearchFragment;
    private Fragment mCollectionFragment;
    private Fragment currentVisibleFragment;
    private boolean mIsInCollectionView;

    public Toolbar getToolbar() {
        return mToolbar;
    }


    private void prepareFragment(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                FragmentManager fm = getSupportFragmentManager();
                if(null == mSearchFragment) {
                    mSearchFragment = SearchFragment.getInstance();
                    fm.beginTransaction().add(R.id.search_fragment_container,mSearchFragment).commit();
                    updateVisibilityOfFragments();
                }
                if(null == mCollectionFragment){
                    mCollectionFragment = CollectionFragment.getInstance();
                    fm.beginTransaction().add(R.id.collection_fragment_container,mCollectionFragment).commit();
                    updateVisibilityOfFragments();
                }
                for(int i=0;i<mMainFragments.length;i++){
                    if(null==mMainFragments[i]){
                        mMainFragments[i] = MainFragment.getInstance(i);
                        fm.beginTransaction().add(R.id.main_fragment_container,mMainFragments[i]).hide(mMainFragments[i]).commit();
                    }
                }
            }
        });
    }

    private void showParticularFragment(int index){
        FragmentManager fm = getSupportFragmentManager();
        if(null == mMainFragments[index]){
            mMainFragments[index] = MainFragment.getInstance(index);
        }
        Fragment fg = mMainFragments[index];
        if(!((MainFragment)fg).isSetType()){
            ((MainFragment) fg).setCategoryIndex(index);
        }
        if(currentVisibleFragment!=fg){
            fm.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
                    .hide(currentVisibleFragment)
                    .show(fg)
                    .commit();
            mToolbar.animate().translationY(0).setInterpolator(new AccelerateInterpolator(5));
            currentVisibleFragment = fg;
        }
    }
    @Override
    protected void onNewIntent(Intent i){
        setIntent(i);
        if(i.getAction().equals(Intent.ACTION_SEARCH)){
            String query = i.getStringExtra(SearchManager.QUERY);
            mIsSearching = true;
            currentQuery = query;
            updateVisibilityOfFragments();
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(MainActivity.this,
                    SearchSuggestionProvider.AUTHORITY,
                    SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query,null);
            FragmentManager fm = getSupportFragmentManager();
            Fragment fg = fm.findFragmentById(R.id.search_fragment_container);
            if(null==fg){
                fg = null==mSearchFragment?SearchFragment.getInstance(query):mSearchFragment;
                fm.beginTransaction().add(R.id.search_fragment_container,fg).commit();
            }else{
                ((SearchFragment)fg).setQuery(query);
            }
            invalidateOptionsMenu();
            return;

        }
        else
            super.onNewIntent(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIsSearching = false;
        mIsInCollectionView = false;
        FragmentManager fm = getSupportFragmentManager();
        Fragment fg = fm.findFragmentById(R.id.main_fragment_container);
        if(fg==null){
            fg = MainFragment.getInstance(MainFragment.ANDROID);
            fm.beginTransaction().add(R.id.main_fragment_container,fg).commit();
        }
        mMainFragments[MainFragment.ANDROID] = fg;
        currentVisibleFragment = fg;
        prepareFragment();

        currentCategoryIndex = MainFragment.ANDROID;
        mCategoryId = Constants.CATEGORY_ID;
        mDrawerLayout = (DrawerLayout)findViewById(R.id.main_activity_drawerLayout);
        mNavigationView = (NavigationView)findViewById(R.id.main_activity_drawerLayout_navigation_bar);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem mit){
                int id = mit.getItemId();
                int i = 0;

                mIsSearching = false;
                for(;i<mCategoryId.length;i++) {
                    if(id==mCategoryId[i]&&i!= currentCategoryIndex){
                        showParticularFragment(i);
                        mToolbar.setTitle(Constants.CATEGORY[i]);
                        currentCategoryIndex = i;
                        break;
                    }
                }
                if(id==R.id.navigation_menu_collection) {
                    mIsInCollectionView = true;
                    currentCategoryIndex = i;
                } else{
                    mIsInCollectionView = false;
                }
                updateVisibilityOfFragments();
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
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchableInfo);
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                mIsSearching = true;
//                currentQuery = query;
//                updateVisibilityOfFragments();
//                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(MainActivity.this,
//                        SearchSuggestionProvider.AUTHORITY,
//                        SearchSuggestionProvider.MODE);
//                suggestions.saveRecentQuery(query,null);
//                FragmentManager fm = getSupportFragmentManager();
//                Fragment fg = fm.findFragmentById(R.id.search_fragment_container);
//                if(null==fg){
//                    fg = null==mSearchFragment?SearchFragment.getInstance(query):mSearchFragment;
//                    fm.beginTransaction().add(R.id.search_fragment_container,fg).commit();
//                }else{
//                    ((SearchFragment)fg).setQuery(query);
//                }
////                searchItem.collapseActionView();
//                invalidateOptionsMenu();
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return true;
//            }
//        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        switch(id){
            case R.id.menu_item_clear_search_history:
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        SearchSuggestionProvider.AUTHORITY,
                        SearchSuggestionProvider.MODE);
                suggestions.clearHistory();
                return true;
            case R.id.menu_item_about:
                Intent i = AboutActivity.newIntent(this);
                startActivity(i);
                return true;
            default:
        }
        return super.onOptionsItemSelected(menuItem);
    }
    private void updateVisibilityOfFragments(){
        FrameLayout mainFragmentContainer = (FrameLayout)findViewById(R.id.main_fragment_container);
        FrameLayout searchFragmentContainer =(FrameLayout)findViewById(R.id.search_fragment_container);
        FrameLayout collectionFragmentContainer = (FrameLayout)findViewById(R.id.collection_fragment_container);

        if(mIsSearching){
            searchFragmentContainer.setVisibility(View.VISIBLE);
            mainFragmentContainer.setVisibility(View.INVISIBLE);
            collectionFragmentContainer.setVisibility(View.INVISIBLE);
            ((BaseFragment)mSearchFragment).refresh();
            mToolbar.setTitle("\"" + currentQuery + "\"");
        }
        else if(mIsInCollectionView) {
            searchFragmentContainer.setVisibility(View.INVISIBLE);
            mainFragmentContainer.setVisibility(View.INVISIBLE);
            collectionFragmentContainer.setVisibility(View.VISIBLE);
            ((BaseFragment)mCollectionFragment).refresh();
            mToolbar.setTitle("Collection");
        }
        else{
            searchFragmentContainer.setVisibility(View.INVISIBLE);
            mainFragmentContainer.setVisibility(View.VISIBLE);
            collectionFragmentContainer.setVisibility(View.INVISIBLE);
            ((BaseFragment)mMainFragments[currentCategoryIndex]).refresh();
            mToolbar.setTitle(Constants.CATEGORY[currentCategoryIndex]);
            }
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
