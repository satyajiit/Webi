package com.satyajit.webi.fragments;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.roger.catloadinglibrary.CatLoadingView;
import com.satyajit.webi.GetterSetter.Links;
import com.satyajit.webi.activity.welcome_home;
import com.satyajit.webi.adapters.LinksAdapter;
import com.satyajit.webi.R;
import com.satyajit.webi.utils.SnackBarCreator;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
public class DashboardFragment extends Fragment {

    TextView clicks,links,label,tv,tv2,textView,textView2;
    RecyclerView recyclerView;
    private List<Links> linksList = new ArrayList<>();
    private LinksAdapter mAdapter;
    public CatLoadingView mView;
    String stored_user;
    FirebaseFirestore db;
    SnackBarCreator sb;
    int clicks_count=0;
    int total_links=0;
    ImageView empty,img2,img3;
    CardView card,card2;
    RelativeLayout RL;

    //Declaration of all the views done here

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);


        initUI(view);  //Initialise the Views



        mAdapter = new LinksAdapter(linksList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
       // recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        //Show the empty views such as gif
        Glide.with(getActivity()).asGif().load(R.drawable.empty).into(empty);
        empty.setVisibility(View.VISIBLE);
        label.setVisibility(View.VISIBLE);
        setVisibility(View.GONE);



        fetchUser();  //Fetch the user info from the local storage / SharedPrefs

        fetchLinksofUser(); //Fetch the Links from the FireStore and set to the adapter


        showAds(view); //Show Admob Ads




        return view;

    }

    void initUI(View v){

        clicks=v.findViewById(R.id.clicks);
        links=v.findViewById(R.id.links);
        recyclerView = v.findViewById(R.id.recyler);
        sb=new SnackBarCreator(v.findViewById(R.id.placeSnackBar),v.getContext());

        img2 = v.findViewById(R.id.img2);
        img3 = v.findViewById(R.id.img3);
        card = v.findViewById(R.id.count);
        card2 = v.findViewById(R.id.crd2);
        empty = v.findViewById(R.id.empty);
        label = v.findViewById(R.id.emt_txt);
        db = FirebaseFirestore.getInstance();
        tv = v.findViewById(R.id.frty);
        tv2 = v.findViewById(R.id.frty2);
        textView  = v.findViewById(R.id.text77);
        textView2 = v.findViewById(R.id.text2);
        RL = v.findViewById(R.id.dash);

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();

        db.setFirestoreSettings(settings);

        mView = new CatLoadingView();
        mView.setCanceledOnTouchOutside(false);


    }


    private void startCountAnimation() {

        //Animate the number of clicks and total webi

        ValueAnimator animator = ValueAnimator.ofInt(0, clicks_count);
        ValueAnimator animator2 = ValueAnimator.ofInt(0, total_links);
        animator.setDuration(3000);
        animator2.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                clicks.setText(animation.getAnimatedValue().toString());
            }
        });

        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                links.setText(animation.getAnimatedValue().toString());
            }
        });

        animator.start();
        animator2.start();
    }




    public void fetchUser(){

        //Get the userID of the User
        SharedPreferences prefs = getActivity().getSharedPreferences("STATS", MODE_PRIVATE);
        stored_user = prefs.getString("auth", "NULL");


    }


    public void fetchLinksofUser(){

        //Fetch Links from the Firebase


        mView.show(getFragmentManager(), "");


        try {

            DocumentReference docRef = db.collection("users").document(stored_user);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        mView.dismiss();
                        RL.setVisibility(View.VISIBLE);
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {


                            String data="",data2="",data3="";

                            if(document.getData().get("webidesi")!=null)
                                data=document.getData().get("webidesi").toString();   //Got the data of webi.desi from the firebase of selected user

                            if(document.getData().get("namedml")!=null)
                                data2=document.getData().get("namedml").toString();  //Got the data of Named.ml from the firebase of selected user

                            if(document.getData().get("namedgq")!=null)
                                data3=document.getData().get("namedgq").toString();  //Got the data of Named.gq from the firebase of selected user


                            if(data.length()>4)  process(data);
                            if (data2.length()>4) process(data2);
                            if (data3.length()>4) process(data3);


                            mAdapter.notifyDataSetChanged();  //Update the Adapter

                            startCountAnimation(); //Animate the Clicks and Total links



                        } else {

                            sb.show(0, "Authentication Failed");
                            cleanSession();
                            startActivity(new Intent(getActivity(), welcome_home.class));
                            getActivity().finish();

                        }
                    } else {
                        sb.show(0, "Something went wrong , Please try again");
                    }
                }
            });
        } catch (Exception e) {

            mView.dismiss();
            sb.show(0, "Something is Wrong CODE - FINAL02");

        }



    }


    void cleanSession(){

        //Invalidate the user

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("STATS", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();




    }

    void setVisibility(int b){

    //Sets Visibility to the views

        clicks.setVisibility(b);
        recyclerView.setVisibility(b);
        links.setVisibility(b);
        img2.setVisibility(b);
        img3.setVisibility(b);
        card.setVisibility(b);
        card2.setVisibility(b);
        textView.setVisibility(b);
        tv.setVisibility(b);
        tv2.setVisibility(b);
        textView2.setVisibility(b);

    }

    void showAds(View v){

        AdView mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    void process(String data){

        //Hide the gif , since we have links
        empty.setVisibility(View.GONE);
        label.setVisibility(View.GONE);
        setVisibility(View.VISIBLE);


        data = data.substring(1, data.length() - 1); //Removed curly brackets

        List<String> list = Lists.newArrayList(Splitter.on("+,").split(data)); //Split the string to URL NAME : LINK DETAILS format.

        List<String> link;  //For the URL NAME
        List<String> link2;  //For the LINK DETAILS part




        for (int i = 0; i < list.size(); i++) {

            //Loop through each Member or URL NAME : LINK DETAILS

            link = Lists.newArrayList(Splitter.on("=").split(list.get(i)));  //Split to get the URL NAME only and its Particular Details.

            link2 = Lists.newArrayList(Splitter.on(",").split(link.get(1)));  //Split the LINK DETAILS to get date,url ...etc

            Links links = new Links(link2.get(0), link.get(0).trim(), link2.get(2), link2.get(3).replace("+", ""), link2.get(1) + "/" + link.get(0).trim()); //Add to the adapter

            clicks_count = Integer.parseInt(link2.get(3).replace("+", ""))+clicks_count;

            total_links++;



            linksList.add(links);  //Add to ArrayList of Adapter

        }




    }


}
