package com.example.labcoc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class downloadActivity extends AppCompatActivity {

    private HttpURLConnection connection;
    Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        final JSONArray jArr = ((MyApplication) getApplication()).mainArray;

        final JSONArray dArr = ((MyApplication) getApplication()).downloadsArray;

        System.out.println("Downloads Array: " + dArr.toString());
        System.out.println("Facilities Array: " + ((MyApplication) getApplication()).facilitiesArray.toString());

        refreshList(dArr);

        refreshButton = findViewById(R.id.refButton);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(downloadActivity.this,downloadActivity.class);
                startActivity(intent);
            }
        });

    }

    private void getJson() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    BufferedReader reader;
                    String line;
                    StringBuffer responseContent = new StringBuffer();

                    try {

                        // Set up and perform get request
                        URL url = new URL("REDACTED");
                        connection = (HttpURLConnection) url.openConnection();

                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(10000);
                        connection.setReadTimeout(10000);

                        int status = connection.getResponseCode();

                        System.out.println("Response code: " + status);

                        if (status> 299) { // Error code
                            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(downloadActivity.this, "ERROR: Cannot connect to web server. Try again.", Toast.LENGTH_SHORT).show();
                                }
                            });

                            while((line = reader.readLine()) != null) {
                                responseContent.append(line);
                            }
                            reader.close();
                        }
                        else { // Populate responseContent with info from get request
                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(downloadActivity.this, "Searching...", Toast.LENGTH_SHORT).show();
                                }
                            });

                            while((line = reader.readLine()) != null) {
                                responseContent.append(line);
                            }
                            reader.close();
                        }

                        //PARSING RESPONSE CONTENT BELOW

                        //System.out.println("Response: ");
                        System.out.println(responseContent.toString());

                        System.out.println("Before array created");
                        JSONObject events = new JSONObject(responseContent.toString());
                        JSONArray eventsArray = new JSONArray(events.getJSONArray("events").toString());
                        System.out.println(eventsArray.length());
                        System.out.println(events.length());

                        ((MyApplication) getApplication()).downloadsArray = eventsArray;

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(downloadActivity.this, "Failed to connect! Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        thread.start();
    }

    public void refreshList(JSONArray array) {
        for(int i = 0; i < array.length(); i++) {
            Button myButton = new Button(downloadActivity.this);
            try {
                myButton.setText(array.getJSONObject(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myButton.setId(i);
            final int id_ = myButton.getId();


            LinearLayout layout = findViewById(R.id.downloadScrollLayout);
            layout.addView(myButton);

            myButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(downloadActivity.this, downloadsShowActivity.class);
                    intent.putExtra("eventID", id_);
                    startActivity(intent);
                }
            });

        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(downloadActivity.this, selectActivity.class);
        startActivity(intent);
    }

}
