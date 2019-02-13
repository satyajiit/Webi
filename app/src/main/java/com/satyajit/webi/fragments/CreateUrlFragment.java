package com.satyajit.webi.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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





public class CreateUrlFragment extends Fragment{

    FloatingActionButton fab;
    final RotateAnimation rotate=new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    //Rotate animation
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    public List<Links> linksList = new ArrayList<>();
    private LinksAdapter mAdapter;
    String stored_user;
    SnackBarCreator sb;
    FirebaseFirestore db;
    public CatLoadingView mView;
    ImageView wait;
    TextView empt,fixed;



    public static CreateUrlFragment newInstance() {
        return new CreateUrlFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_url, container, false);



        initUI(view);  //Initiate the UI

        setListeners(); //Set the OnClick Listeners





        mAdapter = new LinksAdapter(linksList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);





        fetchUser();






        return view;

    }

    @Override
    public void onResume(){
        super.onResume();

        linksList.clear();
        fetchLinksofUser();

    }


    void initUI(View v){

         fab = v.findViewById(R.id.fab);
         rotate.setDuration(500);  //set duration of rotation animations
         rotate.setInterpolator(new LinearInterpolator());  //Interpolator
         swipeRefreshLayout = v.findViewById(R.id.simpleSwipeRefreshLayout);
         swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.colorPrimary));
         fab.startAnimation(rotate);  //Rotate the fab when Create url fragment is accessed
         recyclerView = v.findViewById(R.id.links);
         wait = v.findViewById(R.id.empty);
         empt = v.findViewById(R.id.emt_txt);
          fixed = v.findViewById(R.id.fixed);

         sb=new SnackBarCreator(v.findViewById(R.id.placeSnackBar),v.getContext());

         db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        mView = new CatLoadingView();
        mView.setCanceledOnTouchOutside(false);

    }

    void setListeners(){

        fab.setOnClickListener(new View.OnClickListener() { //Set the Click
            @Override
            public void onClick(View view) {
                //Do Something!
                fab.startAnimation(rotate);  //Rotate when clicked

                CreateLinkPopFragment alertDialogFragment = new CreateLinkPopFragment();

                FragmentManager manager = getFragmentManager();

                alertDialogFragment.show(manager,"fragment_pop");


            }
        });




        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {





                linksList.clear();
                refresh();


            }
        });




        empt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLinksofUser();
            }
        });






    }




    public void fetchUser(){

        //Get the links created by the User
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
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            String data="",data2="",data3="";

                            if(document.getData().get("webidesi")!=null)
                                data=document.getData().get("webidesi").toString();   //Got the data of webi.desi from the firebase of selected user

                            if(document.getData().get("namedml")!=null)
                                data2=document.getData().get("namedml").toString();  //Got the data of Named.ml from the firebase of selected user

                            if(document.getData().get("namedgq")!=null)
                                data3=document.getData().get("namedgq").toString();  //Got the data of Named.gq from the firebase of selected user



                            if(data.length()>4||data2.length()>4||data3.length()>4) {



                                wait.setVisibility(View.GONE);
                                empt.setVisibility(View.GONE);
                                setVisibility(View.VISIBLE);


                                   if(data.length()>4) prs(data);
                                if(data2.length()>4)  prs(data2);
                                if(data3.length()>4)   prs(data3);


                            }
                                else {


                                Glide.with(getActivity()).asGif().load(R.drawable.waiter).into(wait);
                                wait.setVisibility(View.VISIBLE);
                                empt.setVisibility(View.VISIBLE);
                                setVisibility(View.GONE);

                            }

                            mAdapter.notifyDataSetChanged();  //Update the Adapter



                            //sb.show(user_links);
                        } else {

                            sb.show(0, "Authentication Failed");
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




 void refresh(){





     try {

         DocumentReference docRef = db.collection("users").document(stored_user);

         docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 if (task.isSuccessful()) {

                     swipeRefreshLayout.setRefreshing(false);

                     DocumentSnapshot document = task.getResult();
                     if (document.exists()) {


                         String data="",data2="",data3="";

                         if(document.getData().get("webidesi")!=null)
                             data=document.getData().get("webidesi").toString();   //Got the data of webi.desi from the firebase of selected user

                         if(document.getData().get("namedml")!=null)
                             data2=document.getData().get("namedml").toString();  //Got the data of Named.ml from the firebase of selected user

                         if(document.getData().get("namedgq")!=null)
                             data3=document.getData().get("namedgq").toString();  //Got the data of Named.gq from the firebase of selected user




                         if(data.length()>4||data2.length()>4||data3.length()>4) {



                             wait.setVisibility(View.GONE);
                             empt.setVisibility(View.GONE);
                             setVisibility(View.VISIBLE);

                             if(data.length()>4) prs(data);
                             if(data2.length()>4)  prs(data2);
                             if(data3.length()>4)   prs(data3);

                         }


                         else {


                             Glide.with(getActivity()).asGif().load(R.drawable.waiter).into(wait);
                             wait.setVisibility(View.VISIBLE);
                             empt.setVisibility(View.VISIBLE);
                             setVisibility(View.GONE);

                         }

                         mAdapter.notifyDataSetChanged();  //Update the Adapter


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

        recyclerView.setVisibility(b);
        swipeRefreshLayout.setVisibility(b);
        fixed.setVisibility(b);

}

void prs(String data){



    data = data.substring(1, data.length() - 1); //Removed curly brackets

    List<String> list = Lists.newArrayList(Splitter.on("+,").split(data)); //Split the string to URL NAME : LINK DETAILS format.

    List<String> link;  //For the URL NAME
    List<String> link2;  //For the LINK DETAILS part

    //sb.show(list.get(1));


    for (int i = 0; i < list.size(); i++) {

        //Loop through each Member or URL NAME : LINK DETAILS

        link = Lists.newArrayList(Splitter.on("=").split(list.get(i)));  //Split to get the URL NAME only and its Particular Details.

        link2 = Lists.newArrayList(Splitter.on(",").split(link.get(1)));  //Split the LINK DETAILS to get date,url ...etc

        Links links = new Links(link2.get(0), link.get(0).trim(), link2.get(2), link2.get(3).replace("+", ""), link2.get(1) + "/" + link.get(0).trim()); //Add to the adapter

        linksList.add(links);  //Add to ArrayList of Adapter

    }



}


}
