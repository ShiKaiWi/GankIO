package com.example.xkwei.gankio.utils;

import com.example.xkwei.gankio.models.Article;

import java.util.List;

/**
 * Created by xkwei on 30/12/2016.
 */

public class ArticleListFactory {
    private List<Article> mArticles;
    private List<Article> mSpecialArticles;
    private ExtractArticles mExtractArticles;

    interface ExtractArticles{
        List<Article> extractMethod();
    }

    public void setExtractArticles(ExtractArticles ea){
        mExtractArticles = ea;
    }

    public ArticleListFactory(List<Article> articles){
        mArticles = articles;
    }

    public void extract(){
        mSpecialArticles = mExtractArticles.extractMethod();
    }

    public List<Article> getSpecialArticles() {
        return mSpecialArticles;
    }

    public List<Article> getArticles() {
        return mArticles;
    }
}
