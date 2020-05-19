
package com.example.labcoc;

import android.app.Application;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;


//set: ((MyApplication) this.getApplication()).setVariable(thingToSet);
//get: int num = ((MyApplication) this.getApplication()).getVariable();

//SETTERS NEED TO SAVE DATA INTO PERSISTENT MEMORY

public class MyApplication extends Application {

    //Need a JSONArray of JSONObjects (events) each containing a few variables and a JSONArray of JSONObjects (samples) with a few variables
    public JSONArray mainArray = new JSONArray();
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";

    public void saveJson() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, mainArray.toString());
        editor.apply();
    }

    public void loadJson() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        try {
            mainArray = new JSONArray(sharedPreferences.getString(TEXT,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}