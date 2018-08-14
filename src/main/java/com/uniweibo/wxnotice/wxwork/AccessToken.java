package com.uniweibo.wxnotice.wxwork;

import com.google.gson.annotations.SerializedName;

/**
 * @author emacsist
 */
public class AccessToken {
    /**
     * errcode : 0
     * errmsg :
     * access_token : accesstoken000001
     * expires_in : 7200
     */

    private int errcode;
    private String errmsg;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private int expiresIn;


    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(final int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(final String errmsg) {
        this.errmsg = errmsg;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(final int expiresIn) {
        this.expiresIn = expiresIn;
    }
}
