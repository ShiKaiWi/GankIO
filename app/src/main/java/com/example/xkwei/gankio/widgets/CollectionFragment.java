package com.example.xkwei.gankio.widgets;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xkwei.gankio.MainActivity;
import com.example.xkwei.gankio.R;
import com.example.xkwei.gankio.bases.BaseFragment;
import com.example.xkwei.gankio.models.Article;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by xkwei on 14/01/2017.
 */

public class CollectionFragment extends BaseFragment {

    private static final String TAG="CollectionFragment";


    public static Fragment getInstance(){
        return new CollectionFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mToolbar = ((MainActivity)getActivity()).getToolbar();
        mRealm = Realm.getDefaultInstance();
    }


    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container, Bundle savedInstanceState){

        View v = super.onCreateView(lif,container,savedInstanceState);
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if(!mIsRefreshing){
//                    if(dy<0 && getFirstVisiblePosition()==0){
//                        fetchingData();
//                    }
//                }
//            }
//        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecyclerView();
                mSwipeRefreshLayout.setRefreshing(false);
                mIsRefreshing = false;
            }
        });
        return v;
    }


    private void fetchingData(){
        if(mIsRefreshing)return;
        toggleToolbar(true);
        mIsRefreshing = true;
        updateRecyclerView();
    }

    @Override
    protected void updateRecyclerView() {
        RealmResults<Article> items =
                mRealm.where(Article.class).equalTo("mIsLiked",true).findAllSorted("mDate", Sort.DESCENDING);
        ((ArticleRecyclerViewAdapter)mAdapter).updateData(items);
    }



    @Override
    protected void setFragmentLayout(){
        mFragmentLayout = R.layout.fragment_main;
    }

    @Override
    protected void setRecyclerViewAdapter(){
        mAdapter = new ArticleRecyclerViewAdapter(getActivity(),
                mRealm.where(Article.class).equalTo("mIsLiked",true).findAllSorted("mDate", Sort.DESCENDING),false);
    }

}
