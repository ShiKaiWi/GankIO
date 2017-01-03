package com.example.xkwei.gankio;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.xkwei.gankio.widgets.ArticlePageFragment;

/**
 * Created by xkwei on 03/01/2017.
 */

public class ArticlePageActivity extends AppCompatActivity {

    public static Intent newIntent(Context context,Uri uri){
        Intent i = new Intent(context,ArticlePageActivity.class);
        i.setData(uri);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fg = fm.findFragmentById(R.id.main_fragment_container);
        if(fg==null){
            fg = ArticlePageFragment.newInstance(getIntent().getData());
            fm.beginTransaction().add(R.id.main_fragment_container,fg).commit();
        }
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getSupportFragmentManager();
        ArticlePageFragment fragment = (ArticlePageFragment) fm.findFragmentById(R.id.main_fragment_container);
        WebView wv = fragment.getWebView();
        if(wv.canGoBack()){
            wv.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}
