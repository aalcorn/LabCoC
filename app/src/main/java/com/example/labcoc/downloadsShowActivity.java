package com.example.labcoc;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONException;

public class downloadsShowActivity extends AppCompatActivity {
    EditText nameBox;
    EditText samplerNameBox;
    EditText facilityText;

    RadioButton hpcButton;
    RadioButton legButton;

    Button downloadButton;

    int eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads_show);

        final JSONArray jArr = ((MyApplication) getApplication()).mainArray;

        final JSONArray dArr = ((MyApplication) getApplication()).downloadsArray;

        final JSONArray fArr = ((MyApplication) getApplication()).facilitiesArray;

        eventID = getIntent().getExtras().getInt("eventID");

        downloadButton = findViewById(R.id.downEventButton);

        nameBox = findViewById(R.id.nameBox);
        samplerNameBox = findViewById(R.id.samplerNameBox);
        facilityText = findViewById(R.id.facilityText);

        hpcButton = findViewById(R.id.hpcButton);
        legButton = findViewById(R.id.legionButton);

        try {
            nameBox.setText(dArr.getJSONObject(eventID).getString("name"));

            if(dArr.getJSONObject(eventID).getString("type").toLowerCase().equals("hpc")) {
                hpcButton.performClick();
                legButton.setEnabled(false);
            }
            else {
                legButton.performClick();
                hpcButton.setEnabled(false);
            }

            for (int i = 0; i < fArr.length(); i++) {
                System.out.println(dArr.getJSONObject(eventID).getString("facilityID") + "down");
                System.out.println(fArr.getJSONObject(i).getString("_id") + "fac");
                if (dArr.getJSONObject(eventID).getString("facilityID").equals(fArr.getJSONObject(i).getString("_id"))) {
                    facilityText.setText(fArr.getJSONObject(i).getString("name"));
                    System.out.println("Should be updated here");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        nameBox.setFocusable(false);
        samplerNameBox.setFocusable(false);
        facilityText.setFocusable(false);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    jArr.put(dArr.getJSONObject(eventID));
                    System.out.println(jArr.toString());
                    dArr.remove(eventID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((MyApplication) getApplication()).saveJson();
                Intent intent = new Intent(downloadsShowActivity.this, downloadActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onBackPressed() {
        Intent intent = new Intent(downloadsShowActivity.this, downloadActivity.class);
        startActivity(intent);
    }
}
