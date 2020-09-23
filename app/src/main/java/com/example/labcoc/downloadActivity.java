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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class downloadActivity extends AppCompatActivity implements infoEntry.InfoEntryListener {

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
                Toast.makeText(downloadActivity.this,"Searching...",Toast.LENGTH_LONG);
                openDialog();
            }
        });

    }

    private void getDownloads(final String email, final String password) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                InputStream stream = null;
                try {

                    // Set up and perform get request
                    URL url = new URL("http://69.92.212.4/sampling/incomplete");
                    HttpURLConnection downloadsConnection = (HttpURLConnection) url.openConnection();
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

                        Intent intent = new Intent(downloadActivity.this, downloadActivity.class);
                        startActivity(intent);
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(downloadActivity.this, result, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(downloadActivity.this,"Bad URL!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(downloadActivity.this,"Failed to connect to URL!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(downloadActivity.this, "Error finding samples!", Toast.LENGTH_SHORT).show();
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

    public void openDialog() {
        infoEntry infoEntry = new infoEntry();
        infoEntry.show(getSupportFragmentManager(), "Info Entry Dialog");
    }

    @Override
    public void onRecieveInfo(String email, String password) {
        getDownloads(email, password);
    }

}
