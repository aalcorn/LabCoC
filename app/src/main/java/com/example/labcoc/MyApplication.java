
package com.example.labcoc;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;


//set: ((MyApplication) this.getApplication()).setVariable(thingToSet);
//get: int num = ((MyApplication) this.getApplication()).getVariable();

//SETTERS NEED TO SAVE DATA INTO PERSISTENT MEMORY

public class MyApplication extends Application {

    //Need a JSONArray of JSONObjects (events) each containing a few variables and a JSONArray of JSONObjects (samples) with a few variables
    public JSONArray mainArray = new JSONArray();
    public JSONArray downloadsArray = new JSONArray();
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String MAINTEXT = "text";
    public static final String DOWNTEXT = "downtext";

    public void saveDownloads() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(DOWNTEXT, downloadsArray.toString());
        editor.apply();
    }

    //DOWNLOAD ARRAY METHODS

    public void loadDownloads() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        try {
            downloadsArray = new JSONArray(sharedPreferences.getString(DOWNTEXT,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clearDownloads() {
        downloadsArray = new JSONArray();
        saveDownloads();
    }

    public String toDownloadString() {
        return downloadsArray.toString();
    }

    //MAIN ARRAY METHODS

    public void saveJson() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(MAINTEXT, mainArray.toString());
        editor.apply();
    }

    public void loadJson() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        try {
            mainArray = new JSONArray(sharedPreferences.getString(MAINTEXT,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clearJson() {
        mainArray = new JSONArray();
        saveJson();
    }

    public String toString() {
        return mainArray.toString();
    }

    public long getSize() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String data = sharedPreferences.getString(MAINTEXT, "");
        byte[] byteArray = null;
        try {
            byteArray = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return byteArray.length;
    }
}