package com.satyajit.webi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Satyajit
 * Thanks to Java Docs
 * Last Modified 25th January 2019
 * 
 * 
 * Validate the following :
 * 
 * satyajit – No @ symbole
 * satyajit@.com.my – Dot after @ symbol
 * satyajit123@gmail.a – last TLD length is less than 2
 * satyajit123@@.com.com – Two @ symbols
 * .satyajit@satyajit.com – ID can’t start with .
 * satyajit()*@gmail.com – invalid special characters in the ID
 * satyajit@%*.com – invalid special characters in the TLD
 * satyajit..2002@gmail.com – ID can’t have two dots
 * satyajit.@gmail.com – ID can’t end with dot
 * satyajit@satyajit@gmail.com – Two @ symbols
 * satyajit@gmail.com.1a – last TLD can have characters only
 * 
 */


public class EmailValidator {
    // Email Regex java
    private static final String EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$";

    // static Pattern object, since pattern is fixed
    private static Pattern pattern;

    // non-static Matcher object because it's created from the input String
    private Matcher matcher;

    public EmailValidator() {
        // initialize the Pattern object
        pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
    }

    /**
     * This method validates the input email address with EMAIL_REGEX pattern
     *
     * @param email
     * @return boolean
     */
    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
