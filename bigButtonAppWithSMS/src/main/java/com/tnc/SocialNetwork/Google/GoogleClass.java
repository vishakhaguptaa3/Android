package com.tnc.SocialNetwork.Google;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.tnc.fragments.UserInfoFragment;


/**
 * Created by a3logics on 10/11/17.
 */

public class GoogleClass {
    private Activity mActivityRef;
    private String GOOGLE_CLASS_TAG = "googleResultTag";
    private GoogleSignInClient mGoogleSignInClient;
    public static int RESULT_ID = 23;
    private boolean mKeepLoggedIn = false;
    static GoogleClass googleClassInstance;
    GoogleSignInAccount mGoogleSignInAccountData;
    GoogleUserDetailModel mGoogleUserDetailModel;
    GoogleUserInfoInterface mGoogleUserInfoInterface;
    
    public GoogleClass(Activity activity){
        mActivityRef = activity;
        googleClassInstance = this;
    }

    public GoogleClass getInstance(){
        return googleClassInstance;
    }


    /**
     * This function called onActivityResult in activity
     * @param requestCode
     * @param resultCode
     * @param data data contain user Account info
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data){
        if(data!=null && requestCode == GoogleClass.RESULT_ID) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (googleClassInstance != null) {
                googleClassInstance.handleSignInResult(task);
            }
        }
    }

    /**
     *
     * @return return the user data from google
     * @throws NullPointerException the return object can be Null hence user has to handle the exception
     */
    public GoogleUserDetailModel getUserData() throws NullPointerException{
        if(mGoogleUserDetailModel==null) {
            return new GoogleUserDetailModel(mGoogleSignInAccountData);
        }else{
            return mGoogleUserDetailModel;
        }
    }

    /**
     * This function handle will perform the final function on successful data retrieval ,
     * function will call the @taskOnComplete function, to perform the user defined functionality on success
     * @param completedTask
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Log.e("Google Data",account.getId()+" "+account.getDisplayName()+" "+ account.getEmail()+" "+ account.getPhotoUrl());
            mGoogleSignInAccountData = account;
            if(!mKeepLoggedIn) {revokeAccess();}
            mGoogleUserInfoInterface.taskOnComplete(getUserData());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(GOOGLE_CLASS_TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    /**
     * fuction will start the initial process of google login.
     * @param iskeepAccountLoggedIn flag which user want to keep the last logged in account as default logging account in future
     *                              false means user want to show all existing account on each time google login starts
     *                              true means user don't want to show all existing account , where as uses last login account
     * @param googleUserInfoInterface user define functionality which call on result receive from API
     */
    public void googleLogin(boolean iskeepAccountLoggedIn, GoogleUserInfoInterface googleUserInfoInterface){
        mKeepLoggedIn = iskeepAccountLoggedIn;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleUserInfoInterface = googleUserInfoInterface;

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(mActivityRef, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivityRef.startActivityForResult(signInIntent, RESULT_ID);
    }

    /**
     * sign out all logged in account
     */
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(mActivityRef, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
    
            }
        });
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(mActivityRef, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }


}
