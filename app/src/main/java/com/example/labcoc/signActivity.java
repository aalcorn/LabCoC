package com.example.labcoc;

import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;

public class signActivity extends AppCompatActivity {

    Button confButton;
    Bitmap btm;
    GestureOverlayView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        final JSONArray jArr = ((MyApplication) getApplication()).mainArray;

        final int eventID = getIntent().getExtras().getInt("eventID");
        final boolean edited = getIntent().getExtras().getBoolean("editState");

        confButton = findViewById(R.id.confirmSignButton);

        gv = findViewById(R.id.gestureOverlayView);

        confButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setDrawingCacheEnabled(true);
                btm = Bitmap.createBitmap(gv.getDrawingCache());
                gv.setDrawingCacheEnabled(false);

                String bitmapString = BitMapToString(btm);

                try {
                    if (edited) {
                        if (!jArr.getJSONObject(eventID).has("signature")) {
                            jArr.getJSONObject(eventID).put("signature", bitmapString);
                        }
                        jArr.getJSONObject(eventID).put("editSignature", bitmapString);
                        Intent intent = new Intent(signActivity.this, samplesActivity.class);
                        intent.putExtra("eventID", eventID);
                        ((MyApplication) getApplication()).saveJson();
                        startActivity(intent);
                    }
                    else {
                        jArr.getJSONObject(eventID).put("signature", bitmapString);
                        Intent intent = new Intent(signActivity.this, samplesActivity.class);
                        intent.putExtra("eventID", eventID);
                        ((MyApplication) getApplication()).saveJson();
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
}
