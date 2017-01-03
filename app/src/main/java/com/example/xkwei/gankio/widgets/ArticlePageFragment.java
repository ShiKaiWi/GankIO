package com.example.xkwei.gankio.widgets;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.xkwei.gankio.R;

/**
 * Created by xkwei on 03/01/2017.
 */

public class ArticlePageFragment extends Fragment {
    private static final String ARG_URI="Article_Page_Fragment_Article_Uri";
    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    public static ArticlePageFragment newInstance(Uri uri){
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI,uri);
        ArticlePageFragment apf = new ArticlePageFragment();
        apf.setArguments(args);
        return apf;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mUri = getArguments().getParcelable(ARG_URI);
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup vg,Bundle savedInstanceState){
        View v = lif.inflate(R.layout.fragment_article_page,vg,false);
        mWebView = (WebView) v.findViewById(R.id.article_page_fragment_webview);
        mProgressBar = (ProgressBar) v.findViewById(R.id.article_page_fragment_progressbar);

        mWebView.getSettings().setJavaScriptEnabled(true);

        mProgressBar.setMax(100);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView wv,int newProgress){
                if(newProgress==100){
                    mProgressBar.setVisibility(ProgressBar.GONE);
                }
                else{
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView wv,String title){
                AppCompatActivity activity = (AppCompatActivity)getActivity();
                activity.getSupportActionBar().setSubtitle(title);
            }
        });

        mWebView.loadUrl(mUri.toString());
        return v;
    }

    public WebView getWebView() {
        return mWebView;
    }
}
