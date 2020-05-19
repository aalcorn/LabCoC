package com.example.labcoc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class sampleEditActivity extends AppCompatActivity {

    TextView sampleIdText;
    TextView hotColdText;
    TextView volText;
    Button button;
    Button backButton;

    String sampleId; //the sample ID the user enters
    String hotCold;
    String vol;
    int eventID;
    int sID; //The place of the sample in the sampling event's sample array
    boolean edited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_edit);

        eventID = getIntent().getExtras().getInt("eventID");
        sID = getIntent().getExtras().getInt("sampleID") - 1;

        final JSONArray jArr = ((MyApplication) getApplication()).mainArray;
        System.out.println(jArr.toString());

        sampleIdText = findViewById(R.id.SampleIdText);
        hotColdText = findViewById(R.id.hotColdText);
        volText = findViewById(R.id.volText);

        try {
            if(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("sampleID")) {
                sampleIdText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("sampleID"));
                edited = true;
                sampleIdText.setFocusable(false);
            }
            if(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("temp")) {
                hotColdText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("temp"));
                edited = true;
                hotColdText.setFocusable(false);
            }
            if(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("volume")) {
                volText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("volume"));
                edited = true;
                volText.setFocusable(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sampleIdText.addTextChangedListener(textWatcher);
        hotColdText.addTextChangedListener(textWatcher);
        volText.addTextChangedListener(textWatcher);

        button = findViewById(R.id.sampConfButton);

        if(edited) {
            button.setText("EDIT");
            button.setEnabled(true);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add sample params to array and go back to sampling event page corresponding to this sample

                if(!edited) {
                    sampleId = sampleIdText.getText().toString();
                    hotCold = hotColdText.getText().toString();
                    vol = volText.getText().toString();

                    try {
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleID", sampleId);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("temp", hotCold);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("volume", vol);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    System.out.println(jArr.toString());
                    Intent intent = new Intent(sampleEditActivity.this, selectActivity.class);
                    ((MyApplication) getApplication()).saveJson();
                    startActivity(intent);
                }
                else {
                    //allow the user to edit the sample
                    button.setText("confirm");
                    sampleIdText.setFocusable(true);
                    sampleIdText.setFocusableInTouchMode(true);
                    hotColdText.setFocusable(true);
                    hotColdText.setFocusableInTouchMode(true);
                    volText.setFocusable(true);
                    volText.setFocusableInTouchMode(true);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            sampleId = sampleIdText.getText().toString();
                            hotCold = hotColdText.getText().toString();
                            vol = volText.getText().toString();

                            try {
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleID", sampleId);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("temp", hotCold);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("volume", vol);
                                if(!jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("editDate")) {
                                    jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("editDate", "placeholder");
                                }
                                else {
                                    jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("editDate", "placeholder2");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            System.out.println(jArr.toString());
                            Intent intent = new Intent(sampleEditActivity.this, selectActivity.class);
                            ((MyApplication) getApplication()).saveJson();
                            startActivity(intent);

                        }
                    });
                }

            }
        });

        backButton = findViewById(R.id.sampleEditBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sampleEditActivity.this, samplesActivity.class);
                intent.putExtra("eventID", eventID);
                ((MyApplication) getApplication()).saveJson();
                startActivity(intent);
            }
        });

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            sampleId = sampleIdText.getText().toString();
            hotCold = hotColdText.getText().toString();
            vol = volText.getText().toString();


            button.setEnabled(!sampleId.isEmpty() && !hotCold.isEmpty() && !vol.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
