
package com.r00786.stories.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class StoryResponse {

    @SerializedName("results")
    private ArrayList<Result> mResults;

    @SerializedName("userusername")
    private String mUserusername;

    @SerializedName("profile")
    private String profile;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public ArrayList<Result> getResults() {
        return mResults;
    }

    public void setResults(ArrayList<Result> results) {
        mResults = results;
    }


    public String getUserusername() {
        return mUserusername;
    }

    public void setUserusername(String userusername) {
        mUserusername = userusername;
    }

}
