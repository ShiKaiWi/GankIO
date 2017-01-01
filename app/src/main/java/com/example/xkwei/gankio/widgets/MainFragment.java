package com.example.xkwei.gankio.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xkwei.gankio.R;
import com.example.xkwei.gankio.Services.GankIODataService;
import com.example.xkwei.gankio.utils.Constants;

/**
 * Created by xkwei on 01/01/2017.
 */

public class MainFragment extends Fragment{

    public static Fragment getInstance(){
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent i = GankIODataService.newIntentWithType(getActivity(), Constants.ANDROID);
        getActivity().startService(i);
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container, Bundle savedInstanceState){
        View v = lif.inflate(R.layout.fragment_main,container,false);
        return v;
    }

}
