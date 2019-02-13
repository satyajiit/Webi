package com.satyajit.webi.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.satyajit.webi.R;
import com.satyajit.webi.activity.welcome_home;
import com.satyajit.webi.utils.FontChangeCrawler;

import static android.content.Context.MODE_PRIVATE;

public class AbtFragment extends Fragment implements RewardedVideoAdListener {

    private RewardedVideoAd mRewardedVideoAd;

    TextView ad,texxt2;

    TabItem web,store,call;

    TabLayout tab;

    public static AbtFragment newInstance() {
        return new AbtFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.about_fragment, container, false);




        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        showAds(view); //Show Admob Ads

        initUI(view);

        loadRewardedVideoAd();

        setListeners();


        return view;

    }

    void initUI(View view){


        ad = view.findViewById(R.id.vdoAd);
        web = view.findViewById(R.id.webi);
        store = view.findViewById(R.id.store);
        call = view.findViewById(R.id.call);
        tab = view.findViewById(R.id.tab);
        texxt2 = view.findViewById(R.id.texxt2);

    }

    void setListeners(){

        ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRewardedVideoAd.show();
            }
        });

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        linkOpener("https://satyajiit.xyz");
                        break;
                    case 1:
                        linkOpener("https://play.google.com/store/apps/developer?id=SatyaJit%20Pradhan&hl=en");
                        break;
                    case 2:

                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        // Send phone number to intent as data
                        intent.setData(Uri.parse("tel:" + "+918984122606"));
                        // Start the dialer app activity with number
                        startActivity(intent);
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                switch (tab.getPosition()){
                    case 0:
                        linkOpener("https://satyajiit.xyz");
                        break;
                    case 1:
                        linkOpener("https://play.google.com/store/apps/developer?id=SatyaJit%20Pradhan&hl=en");
                        break;
                    case 2:

                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        // Send phone number to intent as data
                        intent.setData(Uri.parse("tel:" + "+918984122606"));
                        // Start the dialer app activity with number
                        startActivity(intent);
                        break;

                }


            }
        });


        texxt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanSession();
                startActivity(new Intent(getActivity(), welcome_home.class));
                getActivity().finish();
            }
        });


    }

    void linkOpener(String link){


        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
         getActivity().startActivity(browserIntent);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        //Set the typeface of the current view by iterating over the views
        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "fonts/cav.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());
    }

    void showAds(View v){

        AdView mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.vdo_ad),
                new AdRequest.Builder().build());
    }


    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(getActivity(), "Thank You!!" , Toast.LENGTH_SHORT).show();
        // Thanks the User.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }


    @Override
    public void onRewardedVideoAdLoaded() {

        ad.setVisibility(View.VISIBLE);

    }

    @Override
    public void onRewardedVideoAdOpened() {



    }

    @Override
    public void onRewardedVideoStarted() {

        ad.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onRewardedVideoAdClosed() {
        // Load the next rewarded video ad.
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoCompleted() {

    }



    void cleanSession(){

        //Invalidate the user

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("STATS", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();




    }


}
