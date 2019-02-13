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
import com.google.firebase.firestore.SetOptions;
import com.roger.catloadinglibrary.CatLoadingView;
import com.satyajit.webi.R;
import com.satyajit.webi.utils.EmailValidator;
import com.satyajit.webi.utils.Encrypt;
import com.satyajit.webi.utils.SnackBarCreator;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText email,password,name; //EditTexts from the Registration layout
    Button register_button;                 //Get the button from the register layout
    SnackBarCreator sb;                     //Snackbar Class
    String validated_pass,validated_name,validated_email,Encrypted_Email,Encrypted_Pass,Encrypted_Name;
    FirebaseFirestore db;
    Encrypt AES = new Encrypt();


    @Override
    protected void onCreate(Bundle savedBundleInstance){
        super.onCreate(savedBundleInstance);




        setContentView(R.layout.register_layout);

        initUI();




    }


    public void startLoginActivity(View v){

        //Start the Register Activity
        startActivity(new Intent(this,LoginActivity.class));
        finish(); //ends the Register Activity

    }




    @Override
    public void onBackPressed() {

        //Catch the key-down / Back
        startActivity(new Intent(this,welcome_home.class)); //Starts the login Activity
        finish(); //Clears the Register Activity before moving to Login Activity
    }

    void initUI(){

        //Initialise the views

        email = findViewById(R.id.register_email);

        name = findViewById(R.id.register_name);

        password = findViewById(R.id.register_password);



        register_button = findViewById(R.id.register_button);

        //Initialise the snackbar class and create the object

        sb=new SnackBarCreator(findViewById(R.id.register_layout),this);

        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);



    }


    public void validateRegistration(View v){

        //Validate the registration process and Push the Data

        //Stage - 1 - Check for Valid name
        if (!validateFirstName(name.getText().toString())){
            sb.show(0,"Please enter a valid name.");
            name.requestFocus(); //Request focus since this field has error
            return;
        }


        //Stage 2 - Check for valid email using regex.
        if (!new EmailValidator().validateEmail(email.getText().toString())) {
            sb.show(0,"Please enter a valid Email.");
            email.requestFocus(); //Request focus since this field has error
            return;
        }

        //Stage 3 - Password Length validation
        if (!isValidPass()) {
            sb.show(0,"The Password is too short!!");
            password.requestFocus(); //Request focus since this field has error
            return;
        }

        validated_pass = password.getText().toString();



        //Passed all validation

        validated_email = email.getText().toString();
        validated_name = name.getText().toString();

        Encrypted_Email = AES.encrypt(validated_email,"awdqWQErf54!%$Sa_9X").replaceAll("\\s+","");
        Encrypted_Pass = AES.encrypt(validated_pass,validated_email);
        Encrypted_Name = AES.encrypt(validated_name,"awdqWQErf54!%$Sa_9X");




        if(!isInternetAvailable()) sb.show(0,"Err..No Internet Available!!");

        verify();


    }


    public boolean validateFirstName( String firstName )
    {
        //Validates the First Name

       try {


           if (Character.isAlphabetic(firstName.charAt(0))) { //Checks for Alphabetic Char for safe process

               firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1); //Change the first char to Caps

               name.setText(firstName); //Updates the EditText

           }

       }

       catch (Exception e){

       }

        return firstName.matches( "[A-Z][a-zA-Z]*" );
    } // end method validateFirstName

    boolean isValidPass(){

        //Check the entered text is valid password or not

        if (password.getText().toString().length()<6) return false; //Checks the length
            return true;

    }

    boolean isInternetAvailable(){

        //Check if has active internet connection

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    void pushData(){

        //Upload the validated info to server

        // Update one field, creating the document if it does not already exist.
        Map<String, Object> data = new HashMap<>();
        data.put("password",Encrypted_Pass);
        data.put("name",Encrypted_Name);
        data.put("links","");
        data.put("status","0");


        db.collection("users").document(Encrypted_Email.replace("/","@@"))
                .set(data, SetOptions.merge());





    }

    void verify(){

        final CatLoadingView mView;
        mView = new CatLoadingView();
        mView.show(getSupportFragmentManager(), "");

        try {

            DocumentReference docRef = db.collection("users").document(Encrypted_Email.replace("/", "@@"));
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        mView.dismiss();
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            sb.show(0, "EmaiL ID already Exists!!");
                            email.requestFocus();
                        } else {
                            //User doesn't exist , Lets Proceed Further
                            pushData(); //Push data to server
                            storeSession();  //Store Login Session
                            sb.show(100, "Registration Successful...");
                        }
                    } else {
                        sb.show(0, "Something went wrong , Please try again");
                    }
                }
            });
        }

catch (Exception e){

                mView.dismiss();
                sb.show(0,"Something is Wrong CODE - REG01");

            }


    }

    void storeSession(){

        //Store the User Login Session

        SharedPreferences.Editor editor = getSharedPreferences("STATS", MODE_PRIVATE).edit();
        editor.putString("auth", Encrypted_Email.replace("/","@@"));
        editor.putString("key",Encrypted_Pass);
        editor.putString("usr",Encrypted_Name);
        editor.putString("status","0");   //Validated Email 0=Not Validated
        editor.commit();




    }


}
