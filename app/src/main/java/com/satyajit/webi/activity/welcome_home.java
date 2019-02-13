package com.satyajit.webi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.satyajit.webi.R;
import com.satyajit.webi.fragments.policy_frag;


public class welcome_home extends AppCompatActivity {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            checkUser();  //Checks if the logged session exists

             setContentView(R.layout.login_welcome_new);


        }






        void checkUser() {

            SharedPreferences sharedPref = getSharedPreferences("STATS", MODE_PRIVATE);
            if (sharedPref.contains("auth") && sharedPref.contains("key") && sharedPref.contains("usr") && sharedPref.contains("status")) {
                startActivity(new Intent(this, MainActivity.class));
                finish();

            }
        }


        public void startRegisterActivity(View v){

            //Start the Register Activity
            startActivity(new Intent(this,RegisterActivity.class)); //Starts the Register Activity
            finish();  //ends the Login Activity

        }

        public void startLoginActivity(View v){

            //Start the Register Activity
            startActivity(new Intent(this,LoginActivity.class)); //Starts the Register Activity
            finish();  //ends the Login Activity

        }


        public void shwPolicy(View v){

            //Start the Register Activity
            policy_frag alertDialogFragment = new policy_frag();

            FragmentManager manager = getSupportFragmentManager();

            alertDialogFragment.show(manager,"fragment_pop");

        }




    }




