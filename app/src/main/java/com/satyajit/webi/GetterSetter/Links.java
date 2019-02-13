package com.satyajit.webi.GetterSetter;

/**
 *
 * Getter and Setter for Links Recycler View
 * By - Satyajit
 * Updated last 29th January
 *
 */


public class Links {
    private String link_name, url, count,date,domain;  //Our Members

    public Links() {
        //Default Constructor
    }

    public Links(String date, String link_name, String url, String count,String domain) {
        this.link_name = link_name;
        this.url = url;
        this.count = count;
        this.date = date;
        this.domain=domain;
    }

    public String getLink_name() {
        return link_name;
    }

    public void setLink_name(String Link_name) {
        this.link_name = Link_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String Url) {
        this.url = Url;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public String getDomain() {
        return domain;
    }




}