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

public class selectActivity extends AppCompatActivity implements infoEntry.InfoEntryListener {

    private HttpURLConnection downloadsConnection;
    private HttpURLConnection facilitiesConnection;
    private HttpURLConnection uploadConnection;

    Button button;
    Button uploadButton;
    Button delButton;
    Button downButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_select);

        ((MyApplication) getApplication()).loadJson();
        ((MyApplication) getApplication()).loadFacs();

        final JSONArray jArr = ((MyApplication) getApplication()).mainArray;

        System.out.println("jArr: " + jArr.toString());

        //populate list with all previous events
        for(int i = 1; i <= jArr.length(); i++) {
            try {
                if(!jArr.getJSONObject(i-1).has("deleted")) {
                    Button myButton = new Button(selectActivity.this);
                    if(jArr.getJSONObject(i-1).has("name")) {
                        myButton.setText(jArr.getJSONObject(i-1).getString("name"));
                    }
                    else {
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
                Intent intent = new Intent(selectActivity.this, signActivity.class);
                startActivity(intent);
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
                //getJson();
                openDialog();
                //getDownloads("joshua@hgsengineeringinc.com", "123456");
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
                        downloadsConnection = (HttpURLConnection) url.openConnection();

                        downloadsConnection.setRequestMethod("GET");
                        downloadsConnection.setConnectTimeout(10000);
                        downloadsConnection.setReadTimeout(10000);

                        int status = downloadsConnection.getResponseCode();

                        System.out.println("Response code: " + status);

                        if (status> 299) { // Error code
                            reader = new BufferedReader(new InputStreamReader(downloadsConnection.getErrorStream()));
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
                            reader = new BufferedReader(new InputStreamReader(downloadsConnection.getInputStream()));

                            while((line = reader.readLine()) != null) {
                                responseContent.append(line);
                            }
                            reader.close();
                        }

                        //PARSING RESPONSE CONTENT BELOW

                        System.out.println(responseContent.toString());

                        System.out.println("Before array created");
                        JSONObject events = new JSONObject(responseContent.toString());
                        JSONArray eventsArray = new JSONArray(events.getJSONArray("events").toString());
                        System.out.println(eventsArray.length());
                        System.out.println(events.length());

                        ((MyApplication) getApplication()).downloadsArray = eventsArray;

                        //IF EVERYTHING SUCCESSFUL, GO TO NEXT PAGE
                        Intent intent = new Intent(selectActivity.this, downloadActivity.class);
                        startActivity(intent);

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

    private void getDownloads(final String email, final String password) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                    InputStream stream = null;
                    try {

                        // Set up and perform get request
                        URL url = new URL("http://69.92.212.4/sampling/incomplete");
                        downloadsConnection = (HttpURLConnection) url.openConnection();
                        downloadsConnection.setRequestMethod("POST");
                        downloadsConnection.setDoOutput(true);
                        downloadsConnection.setDoInput(true);
                        downloadsConnection.setConnectTimeout(10000);
                        downloadsConnection.setReadTimeout(10000);

                        String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                        data += "&" + URLEncoder.encode("password","UTF-8") + "=" + URLEncoder.encode(password,"UTF-8");

                        downloadsConnection.connect();

                        OutputStreamWriter wr = new OutputStreamWriter(downloadsConnection.getOutputStream());
                        wr.write(data);
                        wr.flush();

                        int responseCode = downloadsConnection.getResponseCode();

                        if(responseCode == 500) {
                            try
                            {
                                String line;
                                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(downloadsConnection.getErrorStream() ) );
                                while( (line = bufferedReader.readLine()) != null )
                                {
                                    //System.out.println("Input Stream: ");
                                    System.out.printf("%s\n", line);
                                }
                            }
                            catch( IOException e )
                            {
                                //System.out.println("Error! ");
                                System.err.println( "Error: " + e );
                            }
                        }

                        System.out.println("Response code: " + responseCode);

                        stream = downloadsConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
                        final String result = reader.readLine();
                        System.out.println(result);

                        if(Character.toString(result.charAt(0)).equals("{")) {
                            JSONObject events = new JSONObject(result);
                            JSONArray eventsArray = new JSONArray(events.getJSONArray("events").toString());
                            JSONArray facilityArray = new JSONArray(events.getJSONArray("facilities").toString());
                            System.out.println(eventsArray.length());
                            System.out.println(events.length());

                            ((MyApplication) getApplication()).downloadsArray = eventsArray;
                            ((MyApplication) getApplication()).facilitiesArray = facilityArray;
                            System.out.println("facilities: " + facilityArray.toString());

                            ((MyApplication) getApplication()).saveFacs();

                            Intent intent = new Intent(selectActivity.this, downloadActivity.class);
                            startActivity(intent);
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(selectActivity.this, result, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }


                        wr.close();
                        reader.close();
                        downloadsConnection.disconnect();

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

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(selectActivity.this, "Error finding samples!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
            }
            });
        thread.start();
    }

    public void openDialog() {
        infoEntry infoEntry = new infoEntry();
        infoEntry.show(getSupportFragmentManager(), "Info Entry Dialog");
    }

    @Override
    public void onRecieveInfo(String email, String password) {
        getDownloads(email, password);
    }
}

