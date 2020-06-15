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
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class selectActivity extends AppCompatActivity {

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
                            url =new URL("http://174.126.172.64:3000/sampling");
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setDoOutput(true);

                            String data = URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(jArr.toString(), "UTF-8");

                            connection.connect();

                            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                            wr.write(data);
                            wr.flush();

                            stream = connection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
                            String result = reader.readLine();

                            System.out.println(result);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
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
                Toast.makeText(selectActivity.this, "WIP", Toast.LENGTH_LONG).show();
            }
        });
    }
}
