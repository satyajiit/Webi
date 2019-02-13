package com.satyajit.webi.adapters;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.satyajit.webi.GetterSetter.Links;
import com.satyajit.webi.R;

import java.util.List;

/**
 * Scripted by Satyajit
 * Updated 29th Jan 19
 *
 */


public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.MyViewHolder> {

    //Extending the Recycler View to use it as the required adapter

    private List<Links> linksList;



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date, name, url,count,appLink;
        public CardView crd;
        //Declared all the views from links list row


        public MyViewHolder(View view) {
            super(view);

            //Init
            date = view.findViewById(R.id.date);
            name = view.findViewById(R.id.name);
            url = view.findViewById(R.id.url);
            count = view.findViewById(R.id.count);
            appLink= view.findViewById(R.id.appLink);
            crd = view.findViewById(R.id.cardview);
        }
    }


    public LinksAdapter(List<Links> linksList) {
        this.linksList = linksList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.links_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Links links = linksList.get(position);
        holder.date.setText(links.getDate());
        holder.name.setText(links.getLink_name());
        holder.appLink.setText(links.getDomain());

        final String temp=links.getUrl(); //stored the url temporarily

        if(temp.length()>25)  holder.url.setText(temp.substring(0,25)+"....");  //Check if the url is too long to fit
            else holder.url.setText(temp);   //If small set as it is in the textView


        holder.crd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://"+links.getDomain()));
                v.getContext().startActivity(browserIntent);
            }
        });


        holder.count.setText(links.getCount()); //Set no of clicks

    }

    @Override
    public int getItemCount() {
        return linksList.size();
    }
}