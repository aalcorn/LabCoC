package com.example.labcoc;

import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

public class signActivity extends AppCompatActivity implements infoEntry.InfoEntryListener {

    Button confButton;
    Button clearButton;
    Bitmap btm;
    GestureOverlayView gv;
    JSONArray sendArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        confButton = findViewById(R.id.confirmSignButton);
        confButton.setEnabled(false);

        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.clear(false);
                confButton.setEnabled(false);
            }
        });

        gv = findViewById(R.id.gestureOverlayView);
        gv.addOnGestureListener(new GestureOverlayView.OnGestureListener() {
            @Override
            public void onGestureStarted(GestureOverlayView gestureOverlayView, MotionEvent motionEvent) {
                confButton.setEnabled(true);
            }

            @Override
            public void onGesture(GestureOverlayView gestureOverlayView, MotionEvent motionEvent) {

            }

            @Override
            public void onGestureEnded(GestureOverlayView gestureOverlayView, MotionEvent motionEvent) {

            }

            @Override
            public void onGestureCancelled(GestureOverlayView gestureOverlayView, MotionEvent motionEvent) {

            }
        });

        confButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setDrawingCacheEnabled(true);
                btm = Bitmap.createBitmap(gv.getDrawingCache());
                gv.setDrawingCacheEnabled(false);

                System.out.println(btm.getHeight());
                System.out.println(btm.getWidth());

                Bitmap scaledBitMap = Bitmap.createScaledBitmap(btm, 300,300, true);

                System.out.println(scaledBitMap.getHeight());
                System.out.println(scaledBitMap.getWidth());

                String bitmapString = BitMapToString(scaledBitMap);

                final JSONArray jArr = ((MyApplication) getApplication()).mainArray;

                try {
                    sendArray = new JSONArray(jArr.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendArray.put(new JSONObject());
                try {
                    sendArray.getJSONObject(sendArray.length() - 1).put("bitmap", bitmapString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //postJson(sendArray);

                //login();

                openDialog();

            }




        });

    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public void postJson(final JSONArray theArray, final String email, final String password) {
        //do a POST to josh's server
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection connection = null;
                InputStream stream = null;
                try {
                    //Post to sampling app URL
                    url = new URL("http://69.92.212.4/sampling/app");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setConnectTimeout(10000);
                    connection.setReadTimeout(10000);

                    String data = URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(theArray.toString(), "UTF-8");
                    data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    //password = URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(info.get("password").toString(), "UTF-8");
                    data += "&" + URLEncoder.encode("password","UTF-8") + "=" + URLEncoder.encode(password,"UTF-8");

                    System.out.println(theArray.toString());

                    connection.connect();

                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    int responseCode = connection.getResponseCode();

                    if(responseCode == 413) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(signActivity.this,"Signature size too big!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    else if(responseCode == 500) {
                        try
                        {
                            String line;
                            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( connection.getErrorStream() ) );
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

                    stream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
                    final String result = reader.readLine();

                    System.out.println(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(signActivity.this,result, Toast.LENGTH_SHORT).show();
                        }
                    });

                    //CLEAR IF GOOD RESULT

                    //((MyApplication) getApplication()).clearJson();

                    Intent intent = new Intent(signActivity.this, selectActivity.class);
                    startActivity(intent);

                    wr.close();
                    reader.close();
                    connection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(signActivity.this,"Bad URL!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(signActivity.this,"Failed to connect to URL!", Toast.LENGTH_SHORT).show();
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
        postJson(sendArray, email, password);
    }
}
