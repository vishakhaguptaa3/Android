package com.tnc.SocialNetwork.Facebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.tnc.R;
import com.tnc.bean.NotificationResponseTimer;
import com.tnc.fragments.UserInfoFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by a3logics on 10/11/17.
 */

public class FaceBookClass {
    Activity mActivityRef;
    private CallbackManager mCallbackManager;
    String mImageUrl;
    private Gson gson;
    FacebookOnTaskComplete mFacebookOnTaskComplete;

    public FaceBookClass(Activity activity, CallbackManager callbackManager,FacebookOnTaskComplete facebookOnTaskComplete){
        mActivityRef = activity;
        mCallbackManager = callbackManager;
        mFacebookOnTaskComplete = facebookOnTaskComplete;
        gson = new Gson();
    }

    public String loginFacebook(){
        LoginManager.getInstance().logInWithReadPermissions(mActivityRef, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    //system.out.println("login Result"+loginResult);
                    if(com.facebook.AccessToken.getCurrentAccessToken()!=null && !com.facebook.AccessToken.getCurrentAccessToken().isExpired()){
                        GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {

                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v("Main", response.toString());
                                    Log.e("Main json",object.toString());
                                    JSONObject profile_pic_data = null;
                                    JSONObject profile_pic_url = null;
                                    FaceBookApiResponse faceBookApiResponse = gson.fromJson(object.toString(),FaceBookApiResponse.class);
                                    if(faceBookApiResponse!=null){
                                        mFacebookOnTaskComplete.onTaskComplete(faceBookApiResponse);
                                    }
                                }
                            });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,picture.width(120).height(120)");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException exception) {
                    new AlertDialog.Builder(mActivityRef)
                            .setTitle(R.string.cancelled)
                            .setMessage(exception.getMessage())
                            .setPositiveButton(R.string.txtOk, null)
                            .show();
                }

                private void showAlert() {
                    new AlertDialog.Builder(mActivityRef)
                            .setTitle(R.string.cancelled)
                            .setMessage(R.string.permission_not_granted)
                            .setPositiveButton(R.string.txtOk, null)
                            .show();
                }
            });
            return mImageUrl;
    }

    public void getFacebookImage(LoginResult loginResult){
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                 new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("Main", response.toString());
                        Log.e("Main json",object.toString());
//                        setProfileToView(object);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(120).height(120)");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
