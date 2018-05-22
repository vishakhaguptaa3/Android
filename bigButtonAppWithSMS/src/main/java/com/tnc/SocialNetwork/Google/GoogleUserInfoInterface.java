package com.tnc.SocialNetwork.Google;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by a3logics on 14/11/17.
 */

public interface GoogleUserInfoInterface {

    void taskOnComplete(GoogleUserDetailModel account);
}
