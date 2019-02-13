package com.satyajit.webi.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.satyajit.webi.R;
import com.satyajit.webi.activity.LoginActivity;
import com.satyajit.webi.activity.MainActivity;


/**
 *   SNACKBAR CREATOR - CLASS
 *   Created by Satyajit Pradhan
 *   Last Updated 25th January
 */


public class SnackBarCreator {

    View SnackView;
    Context c;
    Bundle b;

    public SnackBarCreator(View snackbar,Context c){

        this.SnackView=snackbar;
        this.c=c;

    }


    public void show(String msg){
        //In case the the error code not mentioned
        show(1,msg);  //Sets the default as Correct Snackbar
    }

    public void show(int code,String msg) {

        //msg = The Message that is going to be displayed in the Snackbar
        //code = The Error Level / True / False , Where 0 = Negative Snack
        // 1 = Positive Snack


        Typeface Cav = Typeface.createFromAsset(c.getAssets(), "fonts/ubuntu.ttf");


        Snackbar snack = Snackbar.make(SnackView, msg, Snackbar.LENGTH_LONG);

        View snackView = snack.getView();

        if (code == 100){


            snack.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    c.startActivity(new Intent(c, MainActivity.class));
                    ((Activity) c).finish();

                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });
    }

      if(code==0)
        snackView.setBackgroundResource(R.drawable.bg_gradient_error);  //Sets background the error
        else
            snackView.setBackgroundResource(R.drawable.bg_gradient); //Sets background as positive

        TextView snackTextView=snackView.findViewById(android.support.design.R.id.snackbar_text); //Gets the Snackbar TextView

        snackTextView.setTypeface(Cav); //Sets the Typeface of Snackbar TextView

        snackTextView.setTextColor(ContextCompat.getColor(c,R.color.white));  //

        snack.show();





    }



}
