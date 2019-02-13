package com.satyajit.webi.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.satyajit.webi.R;

import java.net.CookieHandler;
import java.net.CookieManager;


public class policy_frag extends DialogFragment  {

    Button acpt,deny;
    WebView policy;

    public policy_frag() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        setCancelable(false); //Disallow cancellation/dismiss of popUp

        View view = inflater.inflate(R.layout.policy_pop, container);  //Inflate the Layout

        initUI(view);


        setListeners();


        String poli="";




        policy.loadData(getString(R.string.html), "text/html", null);




        return view;
    }

    public void initUI(View v){

        acpt = v.findViewById(R.id.acpt);
        deny = v.findViewById(R.id.deny);
        policy = v.findViewById(R.id.policy);
    }


    void setListeners(){

        acpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });





    }




}




