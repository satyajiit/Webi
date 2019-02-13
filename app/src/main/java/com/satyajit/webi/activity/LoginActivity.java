package com.satyajit.webi.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.roger.catloadinglibrary.CatLoadingView;
import com.satyajit.webi.R;
import com.satyajit.webi.utils.EmailValidator;
import com.satyajit.webi.utils.SnackBarCreator;
import com.satyajit.webi.utils.Encrypt;


public class LoginActivity extends AppCompatActivity {


    EditText login_email,login_pass;  //EditText from the layout
    Button login_button;              //Buttons
    SnackBarCreator sb;               //Snackbar Class
    String Email,Pass,Encrypted_Email,Encrypted_Pass;            //Validated and Encrypted Email & Password
    FirebaseFirestore db;
    Encrypt AES = new Encrypt();
    String Result_Name,Result_status; //Response from the Db


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

       checkUser();  //Checks if the logged session exists


        setContentView(R.layout.login_layout);

        initUI();




    }

    public void startRegisterActivity(View v){

        //Start the Register Activity
        startActivity(new Intent(this,RegisterActivity.class)); //Starts the Register Activity
        finish();  //ends the Login Activity

    }




    void initUI(){


        login_button=findViewById(R.id.login_button); //Login Button init here

        login_email=findViewById(R.id.login_email); //Email id

        login_pass=findViewById(R.id.login_password); //Password

        sb=new SnackBarCreator(findViewById(R.id.login_layout),this);

        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();

        db.setFirestoreSettings(settings);


    }



   public void validateLogin(View v){

        //Stage 1 - Check for valid email using regex.
        if(!new EmailValidator().validateEmail(login_email.getText().toString())) {
            sb.show(0,"Please enter a valid Email.");
            login_email.requestFocus();
            return;
        }

        //Stage 2 - Check for valid password.
        if(!isValidPass()) {
            sb.show(0,"The password is invalid!");
            login_pass.requestFocus();
            return;
        }

        //Store the validated email and password for Stage - 3
        Email=login_email.getText().toString();
        Pass=login_pass.getText().toString();

        Encrypted_Email=AES.encrypt(Email,"awdqWQErf54!%$Sa_9X");
        Encrypted_Pass=AES.encrypt(Pass,Email);

        if(isInternetAvailable()){

            //Verify the credentials
             verifyID();

        }

        else
        sb.show(0,"No Internet Connection is Available!"); //No Internet , err


    }


    boolean isValidPass(){

        //Check the entered text is valid password or not

         if (login_pass.getText().toString().length()<6) return false; //Checks the length

         return true;

    }

    boolean isInternetAvailable(){

        //Check if has active internet connection

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    void verifyID(){

        final CatLoadingView mView;
        mView = new CatLoadingView();
        mView.show(getSupportFragmentManager(), "");
try {


    DocumentReference docRef = db.collection("users").document(Encrypted_Email.replace("/", "@@").trim());
    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            mView.dismiss(); //Dismiss the Progress loader
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    if (document.get("password").equals(Encrypted_Pass)) {

                        //sb.show("Logging You In...");
                        Result_Name = document.get("name").toString();
                        Result_status = document.get("status").toString();
                        storeSession();
                        sb.show(100, "Logging Validation Success...");
                    } else sb.show(0, "Invalid Credentials!");

                } else {
                    sb.show(0, "User Doesn\'t exist!!!");
                }
            } else {
                sb.show(0, "Something went Wrong!!");
            }
        }
    });

}

            catch (Exception e){

            mView.dismiss();
                sb.show(0,"Something is Wrong CODE - VID01");

                            }


    }

    void storeSession(){

        //Store the User Login Session

        SharedPreferences.Editor editor = getSharedPreferences("STATS", MODE_PRIVATE).edit();
        editor.putString("auth", Encrypted_Email.replace("/","@@").replaceAll("\\s+",""));
        editor.putString("key",Encrypted_Pass);
        editor.putString("usr",Result_Name);
        editor.putString("status",Result_status);   //Validated Email 0=Not Validated
        editor.commit();

    }

    void checkUser() {

        SharedPreferences sharedPref = getSharedPreferences("STATS", MODE_PRIVATE);
        if (sharedPref.contains("auth") && sharedPref.contains("key") && sharedPref.contains("usr") && sharedPref.contains("status")) {
            startActivity(new Intent(this, MainActivity.class));
            finish();

        }
    }


    @Override
    public void onBackPressed() {

        //Catch the key-down / Back
        startActivity(new Intent(this,welcome_home.class)); //Starts the login Activity
        finish(); //Clears the Register Activity before moving to Login Activity
    }


}
