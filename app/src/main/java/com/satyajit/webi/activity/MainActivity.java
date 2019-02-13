package com.satyajit.webi.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.google.android.gms.ads.MobileAds;
import com.satyajit.webi.R;
import com.satyajit.webi.fragments.AbtFragment;
import com.satyajit.webi.fragments.CreateUrlFragment;
import com.satyajit.webi.fragments.DashboardFragment;


public class MainActivity extends AppCompatActivity {


    Fragment fragment = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //Actions on Nav item is selected

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = CreateUrlFragment.newInstance();
                    break;
                case R.id.navigation_dashboard:
                    fragment = DashboardFragment.newInstance();

                    break;
                case R.id.navigation_about:
                    fragment = AbtFragment.newInstance();
                    break;

            }

            if (fragment != null) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.commit();
            }


            return true;

        }

    };

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            BottomNavigationView navigation =  findViewById(R.id.navigation);
            navigation.setSelectedItemId(R.id.navigation_dashboard);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener); //Set the Click Listener for Nav Bar


            MobileAds.initialize(this, "ca-app-pub-2485899540395345~9845673420");  //AdMob Ads Initialise

            //Set the main fragment i.e Create URL Fragment
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, DashboardFragment.newInstance()); //Set Dashboard fragment as default
            fragmentTransaction.commit();





        }



    }

