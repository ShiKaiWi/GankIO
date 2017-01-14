package com.example.xkwei.gankio.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xkwei.gankio.R;
import com.example.xkwei.gankio.models.Article;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by xkwei on 14/01/2017.
 */

public  class ArticleRecyclerViewAdapter extends RealmRecyclerViewAdapter<Article,ArticleHolder> {
    private Context mContext;
    public ArticleRecyclerViewAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Article> data, boolean autoUpdate){
        super(context,data,autoUpdate);
        mContext = context;
    }
    @Override
    public ArticleHolder onCreateViewHolder(ViewGroup vg, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.article_fragment_main_recyclerview_item,vg,false);
        return new ArticleHolder(v,mContext);
    }

    @Override
    public void onBindViewHolder(ArticleHolder ahd,int position){
        Article article = getData().get(position);
        ahd.bindArticleItem(article);
//            mRecyclerView.requestLayout();
    }

    public ArticleRecyclerViewAdapter(Context context, OrderedRealmCollection<Article> orc){
        super(context,orc,true);
    }
}