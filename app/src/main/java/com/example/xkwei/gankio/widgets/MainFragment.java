package com.example.xkwei.gankio.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xkwei.gankio.ArticlePageActivity;
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

/**
 * Created by xkwei on 01/01/2017.
 */

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private UpdateReceiver mUpdateReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private Realm mRealm;
    private RecyclerView mRecyclerView;
    public static Fragment getInstance(){
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRealm = Realm.getDefaultInstance();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mUpdateReceiver = new UpdateReceiver();
        mLocalBroadcastManager.registerReceiver(mUpdateReceiver,new IntentFilter(GankIODataService.ACTION_UPDATE_DATA));
        Intent i = GankIODataService.newIntentWithType(getActivity(), Constants.ANDROID);
        getActivity().startService(i);
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container, Bundle savedInstanceState){
        View v = lif.inflate(R.layout.fragment_main,container,false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.fragment_main_recycler_view);
        updateRecyclerView();
        return v;
    }

    private void updateRecyclerView() {
        if (isAdded()) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(new ArticleRecyclerViewAdapter(getActivity(), mRealm.where(Article.class).findAllSorted("mDate", Sort.DESCENDING)));
        }
    }
    private class UpdateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            Log.i(TAG,"got the broadcast");
            RealmResults<Article> realmResults = mRealm.where(Article.class).findAll();
            Log.i(TAG,"got "+realmResults.size()+" articles");

        }
    }

    private class ArticleHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mAuthor,mDate,mDescription;
        Article mArticle;

        public ArticleHolder(View v){
            super(v);
            mAuthor = (TextView) v.findViewById(R.id.article_fragment_main_recyclerview_item_author);
            mDate = (TextView) v.findViewById(R.id.article_fragment_main_recyclerview_item_date);
            mDescription = (TextView) v.findViewById(R.id.article_fragment_main_recyclerview_item_description);
            v.setOnClickListener(this);
        }

        public void bindArticleItem(Article article){
            mArticle = article;
            mAuthor.setText(article.getAuthor());
            mDate.setText(DateUtils.dateToString(article.getDate()));
            mDescription.setText(article.getDescription());
        }

        @Override
        public void onClick(View v){
            Intent i = ArticlePageActivity.newIntent(getActivity(), Uri.parse(mArticle.getUrl()));
            startActivity(i);
        }
    }

    private class ArticleRecyclerViewAdapter extends RealmRecyclerViewAdapter<Article,ArticleHolder>{
        @Override
        public ArticleHolder onCreateViewHolder(ViewGroup vg,int viewType){
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.article_fragment_main_recyclerview_item,vg,false);
            return new ArticleHolder(v);
        }

        @Override
        public void onBindViewHolder(ArticleHolder ahd,int position){
            Article article = getData().get(position);
            ahd.bindArticleItem(article);
        }

        public ArticleRecyclerViewAdapter(Context context, OrderedRealmCollection<Article> orc){
            super(context,orc,false);
        }
    }
}
