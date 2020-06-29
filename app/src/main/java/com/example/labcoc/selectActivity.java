package com.example.labcoc;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.DynamicLayout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class selectActivity extends AppCompatActivity {

    private HttpURLConnection connection;

    Button button;
    Button uploadButton;
    Button delButton;
    Button downButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_select);

        ((MyApplication) getApplication()).loadJson();

        final JSONArray jArr = ((MyApplication) getApplication()).mainArray;

        System.out.println("jArr: " + jArr.toString());

        //populate list with all previous events
        for(int i = 1; i <= jArr.length(); i++) {
            try {
                if(!jArr.getJSONObject(i-1).has("deleted")) {
                    Button myButton = new Button(selectActivity.this);
                    try {
                        myButton.setText(jArr.getJSONObject(i-1).getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        myButton.setText("Event #" + i);
                    }
                    myButton.setId(i);
                    final int id_ = myButton.getId();


                    LinearLayout layout = findViewById(R.id.scrollLayout);
                    layout.addView(myButton);

                    myButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Intent intent = new Intent(selectActivity.this, MainActivity.class);
                            intent.putExtra("eventID", id_);
                            ((MyApplication) getApplication()).saveJson();
                            startActivity(intent);
                        }
                    });

                }
                } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jArr.put(new JSONObject());

                Button myButton = new Button(selectActivity.this);
                myButton.setText("Event #"+ jArr.length());
                myButton.setId(jArr.length());
                final int id_ = myButton.getId();

                final LinearLayout layout = findViewById(R.id.scrollLayout);
                layout.addView(myButton);

                final ScrollView scrollView = findViewById(R.id.scrollView);

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(scrollView.FOCUS_DOWN);
                    }
                });

                myButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent(selectActivity.this, MainActivity.class);
                        intent.putExtra("eventID", id_);
                        ((MyApplication) getApplication()).saveJson();
                        startActivity(intent);
                }
            });

        }

    });

        uploadButton = findViewById(R.id.buttonUpload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                //do a POST to josh's server
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        URL url = null;
                        HttpURLConnection connection = null;
                        InputStream stream = null;
                        try {
                            //url =new URL("http://174.126.172.64:3000/sampling");
                            url = new URL("http://69.92.212.4/sampling/app");
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setDoOutput(true);
                            connection.setDoInput(true);

                            String data = URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(jArr.toString(), "UTF-8");

                            connection.connect();

                            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                            wr.write(data);
                            wr.flush();

                            stream = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
                            String result = reader.readLine();

                            System.out.println(result);

                            wr.close();
                            reader.close();
                            connection.disconnect();
                            //Toast.makeText(selectActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(selectActivity.this,"Bad URL!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(selectActivity.this,"Failed to connect to URL!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });
                thread.start();
            }
        });

        delButton = findViewById(R.id.selectDelButton);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pop up a window that asks if they're sure they want to delete; if they are, add delete flag to the sample
                new AlertDialog.Builder(selectActivity.this)
                        .setTitle("")
                        .setMessage("WARNING: Delete all Sampling Events?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(selectActivity.this,"Deleted", Toast.LENGTH_LONG).show();
                                ((MyApplication) getApplication()).clearJson();
                                System.out.println(((MyApplication) getApplication()).mainArray.toString() + " is the array");
                                Intent intent = new Intent(selectActivity.this, selectActivity.class);
                                startActivity(intent);
                            }
                        })

                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        downButton = findViewById(R.id.DownloadButton);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("JSON size: " + ((MyApplication) getApplication()).getSize());
                //Toast.makeText(selectActivity.this, "WIP", Toast.LENGTH_LONG).show();
                //getJson();
                getFacilities();
                Intent intent = new Intent(selectActivity.this, downloadActivity.class);
                startActivity(intent);
            }
        });
    }
    public void onBackPressed() {
        return;
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
                        URL url = new URL("http://69.92.212.4/sampling/incomplete");
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
                                    Toast.makeText(selectActivity.this, "ERROR: Cannot connect to web server. Try again.", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(selectActivity.this, "Searching...", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(selectActivity.this, "Failed to connect! Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        thread.start();
    }

    public void getFacilities() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    BufferedReader reader;
                    String line;
                    StringBuffer responseContent = new StringBuffer();

                    try {

                        // Set up and perform get request
                        URL url = new URL("http://69.92.212.4/facilities/app");
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
                                    Toast.makeText(selectActivity.this, "ERROR: Cannot connect to web server. Try again.", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(selectActivity.this, "Searching...", Toast.LENGTH_SHORT).show();
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
                        JSONObject facilities = new JSONObject(responseContent.toString());
                        System.out.println("facilities: " + facilities.toString());

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
                            Toast.makeText(selectActivity.this, "Failed to connect! Try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        thread.start();
    }
}

