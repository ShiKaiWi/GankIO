package com.example.xkwei.gankio.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xkwei.gankio.MainActivity;
import com.example.xkwei.gankio.R;
import com.example.xkwei.gankio.bases.BaseFragmentWithUpdater;
import com.example.xkwei.gankio.models.Article;
import com.example.xkwei.gankio.services.GankIODataService;
import com.example.xkwei.gankio.utils.Constants;

import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by xkwei on 01/01/2017.
 */

public class MainFragment extends BaseFragmentWithUpdater {

    private static final String TAG = "MainFragment";
    private int mCategoryIndex;
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

//    @Override
//    public void onAttach(Context context){
//        super.onAttach(context);
//        mToolbar = ((MainActivity)getActivity()).getToolbar();
//        fetchingData(REFRESHING);
//    }

    @Override
    protected void updateIntentFilter(){
        mIntentFilter.addAction(GankIODataService.ACTION_UPDATE_DATA);
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mIsLoadingMore = mIsRefreshing = false;
        updateIntentFilter();
        mLocalBroadcastManager.registerReceiver(mUpdateReceiver,mIntentFilter);
        mPageNumber = 1;
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container, Bundle savedInstanceState){
        View v = super.onCreateView(lif,container,savedInstanceState);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!isFetching()){
                    if(dy>0 && getLastVisiblePosition()==mAdapter.getItemCount()-1)
                    {
                        fetchingData(LOADING_MORE);
                    }
                }
            }
        });
        Bundle args = getArguments();
        if(null!=args) {
            mIsSetType = true;
            mCategoryIndex = args.getInt(CATEGORY);
            mToolbar = ((MainActivity) getActivity()).getToolbar();
            fetchingData(REFRESHING);
        }
        else
            mIsSetType = false;

        return v;
    }

    @Override
    protected void setRecyclerViewAdapter(){
        mAdapter = new ArticleRecyclerViewAdapter(getActivity(),
                mRealm.where(Article.class).findAllSorted("mDate", Sort.DESCENDING),false);
    }

    @Override
    protected void fetchingData(int requestCode){
        if(isFetching())return;
        toggleToolbar(true);
        Intent i = null;
        if(requestCode==REFRESHING){
            mIsRefreshing = true;
            mSwipeRefreshLayout.setRefreshing(true);
            i = GankIODataService.newIntentWithTypeAndPage(getActivity(), Constants.CATEGORY[mCategoryIndex],1);
        }
        else if(requestCode==LOADING_MORE){
            mIsLoadingMore = true;
            mSwipeRefreshLayout.setRefreshing(true);
            i = GankIODataService.newIntentWithTypeAndPage(getActivity(), Constants.CATEGORY[mCategoryIndex],++mPageNumber);
        }
        if(null!=i)
            getActivity().startService(i);
    }


    @Override
    protected void handleReceivedBroadCast(Intent intent){
        if(!intent.getStringExtra(GankIODataService.EXTRA).equals(Constants.CATEGORY[mCategoryIndex]))
            return;
        super.handleReceivedBroadCast(intent);
        Log.i(TAG,"got the broadcast");
        String action = intent.getAction();
        if(action==GankIODataService.ACTION_UPDATE_DATA) {
            RealmResults<Article> realmResults = mRealm.where(Article.class).findAll();
            Log.i(TAG, "got " + realmResults.size() + " articles");
            updateRecyclerView();
        }
    }


    @Override
    protected void updateRecyclerView() {
        RealmResults<Article> items = mRealm.where(Article.class).equalTo("mType",Constants.CATEGORY[mCategoryIndex]).findAllSorted("mDate", Sort.DESCENDING);
        ((ArticleRecyclerViewAdapter)mAdapter).updateData(items);
    }

    @Override
    protected void setFragmentLayout(){
        mFragmentLayout = R.layout.fragment_main;
    }
}
