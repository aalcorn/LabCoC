package com.example.labcoc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;

public class downloadsShowActivity extends AppCompatActivity {
    EditText nameBox;
    EditText expectedSamplesBox;
    EditText facilityText;
    EditText analyteText;
    EditText biocideText;
    EditText testCodeText;
    EditText volumeText;

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
        expectedSamplesBox = findViewById(R.id.expectedSamplesBox);
        facilityText = findViewById(R.id.facilityText);
        analyteText = findViewById(R.id.analyteText);
        biocideText = findViewById(R.id.downloadBiocideEditText);
        testCodeText = findViewById(R.id.downloadTestCodeEditText);
        volumeText = findViewById(R.id.downloadVolumeEditText);

        try {
            nameBox.setText(dArr.getJSONObject(eventID).getString("name"));
            if (dArr.getJSONObject(eventID).has("anticipatedSamples")) {
                if (dArr.getJSONObject(eventID).getString("anticipatedSamples").equals("null")) {
                    dArr.getJSONObject(eventID).put("anticipatedSamples", "0");
                }
                expectedSamplesBox.setText(dArr.getJSONObject(eventID).getString("anticipatedSamples"));
            }

            analyteText.setText(dArr.getJSONObject(eventID).getString("type"));
            biocideText.setText(dArr.getJSONObject(eventID).getString("biocideType"));
            testCodeText.setText(dArr.getJSONObject(eventID).getString("testCode"));
            volumeText.setText(dArr.getJSONObject(eventID).getString("volume"));

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
        expectedSamplesBox.setFocusable(false);
        facilityText.setFocusable(false);
        analyteText.setFocusable(false);
        biocideText.setFocusable(false);
        testCodeText.setFocusable(false);
        volumeText.setFocusable(false);

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
