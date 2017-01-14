package com.example.xkwei.gankio.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xkwei.gankio.ArticlePageActivity;
import com.example.xkwei.gankio.MainActivity;
import com.example.xkwei.gankio.R;
import com.example.xkwei.gankio.models.Article;
import com.example.xkwei.gankio.services.GankIODataService;
import com.example.xkwei.gankio.utils.Constants;
import com.example.xkwei.gankio.utils.DateUtils;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;
import java.lang.Math;
/**
 * Created by xkwei on 01/01/2017.
 */

public class MainFragment extends Fragment {
    private static final int REFRESHING= 0 ;
    private static final int LOADING_MORE= 1;

    private static final String TAG = "MainFragment";
    private UpdateReceiver mUpdateReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private Realm mRealm;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private boolean mIsRefreshing;
    private boolean mIsLoadingMore;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mPageNumber;
    private int mCategoryIndex;
    private Toolbar mToolbar;
    private boolean mIsSetType;

    public static final String CATEGORY="MainFragment_Category_Index";
    public static final int ANDROID = 0;
    public static final int iOS = 1;
    public static final int WEB = 2;
    public static final int APP = 3;
    public static final int CATEGORY_NUM = 4;

    public static Fragment getInstance(int categoryIndex){
        Bundle args = new Bundle();
        args.putInt(CATEGORY,categoryIndex);
        Fragment fg = new MainFragment();
        fg.setArguments(args);
        return fg;
    }
    public static Fragment getInstance(){
        return new MainFragment();
    }

    public boolean isSetType() {
        return mIsSetType;
    }

    public void setCategoryIndex(int categoryIndex) {
        mCategoryIndex = categoryIndex;
        mIsSetType = true;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mToolbar = ((MainActivity)getActivity()).getToolbar();
        fetchingData(REFRESHING);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mIsLoadingMore = mIsRefreshing = false;
        mRealm = Realm.getDefaultInstance();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mUpdateReceiver = new UpdateReceiver();
        mLocalBroadcastManager.registerReceiver(mUpdateReceiver,new IntentFilter(GankIODataService.ACTION_UPDATE_DATA));
        mPageNumber = 1;

        Bundle args = getArguments();
        if(null!=args) {
            mIsSetType = true;
            mCategoryIndex = args.getInt(CATEGORY);
            mToolbar = ((MainActivity) getActivity()).getToolbar();
            fetchingData(REFRESHING);
        }
        else
            mIsSetType = false;
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container, Bundle savedInstanceState){
        View v = lif.inflate(R.layout.fragment_main,container,false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.fragment_main_recycler_view);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private final int THRESH_HOLD = 12;
            private boolean isToolBarVisible=true;
            private int deltaY=0;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(isToolBarVisible && dy>0 || !isToolBarVisible && dy<0){
                    deltaY+=dy;
                }
                if(Math.abs(deltaY)>THRESH_HOLD) {
                    if(getFirstVisiblePosition()!=0) {
                        toggleToolbar(!isToolBarVisible);
                        isToolBarVisible = !isToolBarVisible;
                    }
                    deltaY = 0;
                }
                if(!isFetching()){
                    if(dy<-30 && getFirstVisiblePosition()==0){
                        fetchingData(REFRESHING);
                    }
                    else if(dy>0 && getLastVisiblePosition()==mAdapter.getItemCount()-1)
                    {
                        fetchingData(LOADING_MORE);
                    }
                }
            }
        });


        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ArticleRecyclerViewAdapter(getActivity(), mRealm.where(Article.class).findAllSorted("mDate", Sort.DESCENDING),false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater mif){
//        super.onCreateOptionsMenu(menu,mif);
//        mif.inflate(R.menu.activity_main_menu,menu);
//
//        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
//        mSearchView = (SearchView) searchItem.getActionView();
//
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
//            @Override
//            public boolean onQueryTextChange(String text){
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextSubmit(String query){
//
//                return true;
//            }
//        });
//    }

    private void fetchingData(int requestCode){
        if(isFetching())return;
        toggleToolbar(true);
        ((MainActivity)getActivity()).showProgressBar();
        Intent i = null;
        if(requestCode==REFRESHING){
            mIsRefreshing = true;
            i = GankIODataService.newIntentWithTypeAndPage(getActivity(), Constants.CATEGORY[mCategoryIndex],1);
        }
        else if(requestCode==LOADING_MORE){
            mIsLoadingMore = true;
            i = GankIODataService.newIntentWithTypeAndPage(getActivity(), Constants.CATEGORY[mCategoryIndex],++mPageNumber);
        }
        if(null!=i)
            getActivity().startService(i);
    }

    private int getFirstVisiblePosition(){
        LinearLayoutManager llm = (LinearLayoutManager) mLayoutManager;
        return llm.findFirstVisibleItemPosition();
    }
    private int getLastVisiblePosition(){
        LinearLayoutManager llm = (LinearLayoutManager) mLayoutManager;
        return llm.findLastVisibleItemPosition();
    }
    @Override
    public void onPause(){
        super.onPause();
        mLocalBroadcastManager.unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GankIODataService.ACTION_UPDATE_DATA);
        mLocalBroadcastManager.registerReceiver(mUpdateReceiver,intentFilter);
        mToolbar = ((MainActivity)getActivity()).getToolbar();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }


    private boolean isFetching(){
        return mIsRefreshing||mIsLoadingMore;
    }
    private void setIsFetching(boolean isFetcing){
        mIsLoadingMore = isFetcing;
        mIsRefreshing = isFetcing;
    }
    private class UpdateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            Log.i(TAG,"got the broadcast");
            String action = intent.getAction();
            if(action==GankIODataService.ACTION_UPDATE_DATA) {
                RealmResults<Article> realmResults = mRealm.where(Article.class).findAll();
                Log.i(TAG, "got " + realmResults.size() + " articles");
                updateRecyclerView();
                ((MainActivity)getActivity()).hideProgressBar();
                setIsFetching(false);
            }
        }
    }


    private void updateRecyclerView() {
        RealmResults<Article> items = mRealm.where(Article.class).equalTo("mType",Constants.CATEGORY[mCategoryIndex]).findAllSorted("mDate", Sort.DESCENDING);
        ((ArticleRecyclerViewAdapter)mAdapter).updateData(items);
    }

    private void toggleToolbar(boolean shouldBeVisible){
        int deltaY = shouldBeVisible?0:-mToolbar.getHeight();
        mToolbar.animate().translationY(deltaY).setInterpolator(new AccelerateInterpolator(2));
    }
}
