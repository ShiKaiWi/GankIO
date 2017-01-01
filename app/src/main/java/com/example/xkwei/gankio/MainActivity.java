package com.example.xkwei.gankio;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.xkwei.gankio.widgets.MainFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment createFragment(){
        return MainFragment.getInstance();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fg = fm.findFragmentById(R.id.main_fragment_container);
        if(fg==null){
            fg = createFragment();
        }
        fm.beginTransaction().add(R.id.main_fragment_container,fg).commit();
    }
}
