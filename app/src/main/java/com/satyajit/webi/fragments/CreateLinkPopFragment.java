package com.satyajit.webi.fragments;




import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.roger.catloadinglibrary.CatLoadingView;
import com.satyajit.webi.activity.LoginActivity;
import com.satyajit.webi.R;
import com.satyajit.webi.utils.SnackBarCreator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.Context.MODE_PRIVATE;

public class CreateLinkPopFragment extends DialogFragment {

    Spinner spinner;
    Button cancel, create;
    EditText link_name, link_url;
    FirebaseFirestore db;
    SnackBarCreator sb;
    String validated_url_name, validated_url;
    int verified_name = 0;
    String stored_user;
    String domain="Webi.desi";


    public CreateLinkPopFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setCancelable(false); //Disallow cancellation/dismiss of popUp

        View view = inflater.inflate(R.layout.create_link_pop, container);  //Inflate the Layout



        initUI(view);  //Initialise the views

        setListerners(); //Set listeners

         checkClipboard();

        return view;
    }

    void initUI(View v) {


        spinner = v.findViewById(R.id.shorten_domain);
        create = v.findViewById(R.id.shorten_button_create);
        cancel = v.findViewById(R.id.shorten_button_cancel);
        link_name = v.findViewById(R.id.url_name);
        link_url = v.findViewById(R.id.link_url);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.domain_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        sb = new SnackBarCreator(v.findViewById(R.id.pop_layout), v.getContext());

        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();

        db.setFirestoreSettings(settings);


    }

    void setListerners() {

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });


        link_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isAlphaorDigit(s.toString())) link_name.setError("Use only Alphabets");

            }
        });


        link_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    verified_name = 0;
                    link_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    link_name.setTextColor(getResources().getColor(R.color.webiBlack));
                } else {
                    if (isAlphaorDigit(link_name.getText().toString()) && link_name.getText() != null && link_name.getText().toString() != "") {
                        validated_url_name = link_name.getText().toString();

                        if (!validated_url_name.isEmpty() && validated_url_name.length() > 3) {
                            isNameAvailable();
                        } else {
                            link_name.setError("Use more than three characters");

                        }


                    }
                }
            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verified_name != 1) sb.show(0, "Complete the fields correctly!");
                else if (!isValidURL()) {
                    sb.show(0, "Invalid URL");
                    link_url.requestFocus();
                } else {
                    //All validations are done here
                    //Now Upload the data to server and create the LINK

                    fetchUserID();  //Fetch User ID from the Shared Pref

                    validateUserID();  //Verify if the user exists

                    if (stored_user == "null") {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }


                }
            }
        });





        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                switch (position){

                    case 0:
                        domain = "Webi.Desi";
                        break;
                    case 1:
                        domain = "Named.ml";
                        break;
                    case 2:
                        domain = "Named.gq";
                        break;

                }

                link_name.setHint(domain+"/Your Link name");


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






    }


    void isNameAvailable() {

        //Check if the link name is available



        try {

            DocumentReference docRef = db.collection(domain.replace(".","").toLowerCase()).document(validated_url_name);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //Link name already taken
                            verified_name = 0;
                            link_name.setError("Not Available");
                            sb.show(0, "Name Already Used!");

                        } else {
                            //Link doesn't exist , Lets Proceed Further
                            link_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0);
                            link_name.setTextColor(getResources().getColor(R.color.green));
                            sb.show("Available!");
                            verified_name = 1;
                        }
                    } else {

                        link_name.setText("");
                        sb.show(0, "Something went wrong , Try again");
                    }
                }
            });
        } catch (Exception e) {
            e.getStackTrace();
            Log.d("HEY", e.toString());
            sb.show(0, "Something is Wrong CODE - POP01");

        }


    }


    public boolean isAlphaorDigit(String name) {

        //Allow only alphabets and numbers

        char[] chars = name.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c))
                if(!Character.isDigit(c))
                   return false;

        }

        return true;
    }

    public boolean isValidURL() {

        //Validate the url

        String tmp=link_url.getText().toString();

        if(!tmp.contains("http")||!tmp.contains("https"))
            tmp="https://"+tmp;

        if (URLUtil.isValidUrl(tmp) && tmp.contains(".")&&tmp.contains("http")) {
            validated_url = tmp;
            return true;
        } else
            return false;


    }

    void pushDataToFirebase() {

        //Store to "LINKS" FireStore

        Map<String, Object> data = new HashMap<>();
        data.put("url", validated_url);
        db.collection(domain.replace(".","").toLowerCase()).document(validated_url_name)
                .set(data, SetOptions.merge());

        uploadToPHPServer();  //Create the LINK , call the API




        dismiss();


    }

    void checkFinal() {

        final CatLoadingView mView;
        mView = new CatLoadingView();
        mView.show(getFragmentManager(), "");

        try {

            DocumentReference docRef = db.collection("users").document(validated_url_name);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        mView.dismiss();
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            verified_name = 0;
                            link_name.setError("Name Taken!!");
                        } else {
                            //Link doesn't exist , Lets Proceed Further
                            pushDataToFirebase(); //Push data to server

                        }
                    } else {
                        sb.show(0, "Something went wrong , Please try again");
                    }
                }
            });
        } catch (Exception e) {

            mView.dismiss();
            sb.show(0, "Something is Wrong CODE - FINAL01");

        }


    }

    void pushData() {
        Map<String, Object> data = new HashMap<>();


        Map<String, Object> nestedData = new HashMap<>();

       

        nestedData.put(validated_url_name, getFormattedDate()+","+domain+","+validated_url+",0+");

        data.put(domain.replace(".","").toLowerCase(), nestedData);

        db.collection("users").document(stored_user)
                .set(data, SetOptions.merge());

        checkFinal();

    }


    void fetchUserID() {

        SharedPreferences prefs = getActivity().getSharedPreferences("STATS", MODE_PRIVATE);
        stored_user = prefs.getString("auth", "NULL").replaceAll("\\s+", "");



    }

    void validateUserID() {

        //Verify the user account

        final CatLoadingView mView;
        mView = new CatLoadingView();
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

                            pushData();
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


    public void uploadToPHPServer() {


        // Instantiate the RequestQueue.

        final CatLoadingView mView;
        mView = new CatLoadingView();
        mView.show(getFragmentManager(), "");


        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://"+domain+"/YOUR_PHP_API_OR_SCRIPT",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       mView.dismiss();
                       dismiss();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        mView.dismiss();
                        Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("id",stored_user);
                params.put("url",validated_url);
                params.put("name", validated_url_name);
                return params;
            }

        };

        queue.add(stringRequest);




    }



    public static String getFormattedDate(){

        //Formatted Date , so that can be displayed in the adapter

        Date date = new Date();

        Calendar cal=Calendar.getInstance();
        cal.setTime(date);

        int day=cal.get(Calendar.DATE);

        if(!((day>10) && (day<19)))
            switch (day % 10) {
                case 1:
                    return new SimpleDateFormat("MMM d'st' yyyy").format(date);
                case 2:
                    return new SimpleDateFormat("MMM d'nd' yyyy").format(date);
                case 3:
                    return new SimpleDateFormat("MMM d'rd' yyyy").format(date);
                default:
                    return new SimpleDateFormat("MMM d'th' yyyy").format(date);
            }
        return new SimpleDateFormat("MMM d'th' yyyy").format(date);
    }


void checkClipboard(){

    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
    String pasteData = "";

    // If it does contain data, decide if you can handle the data.
    if (!(clipboard.hasPrimaryClip())) {

    } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

        // since the clipboard has data but it is not plain text

    } else {

        //since the clipboard contains plain text.
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

        // Gets the clipboard as text.
        pasteData = item.getText().toString();

        if (pasteData.contains("https://"))
            link_url.setText(pasteData);

    }





}



}



