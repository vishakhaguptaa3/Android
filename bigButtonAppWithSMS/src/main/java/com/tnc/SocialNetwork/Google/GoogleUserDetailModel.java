package com.tnc.SocialNetwork.Google;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by a3logics on 14/11/17.
 */

public class GoogleUserDetailModel {
    private GoogleSignInAccount mAccountDetail;

    GoogleUserDetailModel(GoogleSignInAccount account){
        mAccountDetail = account;
    }

    public String getId() {
        return mAccountDetail!=null?mAccountDetail.getId():"";
    }

    public String getIdToken() {
        return mAccountDetail!=null?mAccountDetail.getIdToken():"";
    }

    public String getEmail() {
        return mAccountDetail!=null?mAccountDetail.getEmail():"";
    }

    public String getDisplayName() {
        return mAccountDetail!=null?mAccountDetail.getDisplayName():"";
    }

    public String getGivenName() {
        return mAccountDetail!=null?mAccountDetail.getGivenName():"";
    }

    public String getPhotoUrl() {
        return mAccountDetail!=null && mAccountDetail.getPhotoUrl()!=null?mAccountDetail.getPhotoUrl().toString():"";
    }

    public String getFamilyName() {
        return mAccountDetail!=null?mAccountDetail.getFamilyName():"";
    }

    @Override
    public String toString() {
        return  "UserName"+getDisplayName()+"\n"+
                "UserId"+getId()+"\n"+
                "UserEmail"+getEmail()+"\n"+
                "UserFamilyName"+getFamilyName()+"\n"+
                "UserGivenName"+getGivenName()+"\n"+
                "UserPhotoUrl"+getPhotoUrl()+"\n";
    }
}
