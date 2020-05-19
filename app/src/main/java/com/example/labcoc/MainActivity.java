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
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public String sampName;
    public String samplerName;
    public String date;
    public String sampType;
    private int eventID;

    TextView sampNameText;
    TextView samplerNameText;
    TextView dateText;
    TextView sampTypeText;
    Button button;
    Button editButton;
    boolean edited = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventID = getIntent().getExtras().getInt("eventID") - 1;

        final JSONArray jArr = ((MyApplication) getApplication()).mainArray;


        sampNameText = findViewById(R.id.nameBox);
        samplerNameText = findViewById(R.id.samplerNameBox);
        dateText = findViewById(R.id.dateBox);
        sampTypeText = findViewById(R.id.sampTypeText);
        button = findViewById(R.id.confButton);
        button.setEnabled(false);

        //if the event has previous data, fill the textboxes with it and don't allow them to edit it unless they hit edit

        try {
            if(jArr.getJSONObject(eventID).has("name")) {
                sampNameText.setText(jArr.getJSONObject(eventID).getString("name"));
                edited = true;
                sampNameText.setFocusable(false);
            }
            if(jArr.getJSONObject(eventID).has("samplerName")) {
                samplerNameText.setText(jArr.getJSONObject(eventID).getString("samplerName"));
                edited = true;
                samplerNameText.setFocusable(false);
            }
            if(jArr.getJSONObject(eventID).has("samplingDate")) {
                dateText.setText(jArr.getJSONObject(eventID).getString("samplingDate"));
                edited = true;
                dateText.setFocusable(false);
            }
            if(jArr.getJSONObject(eventID).has("type")) {
                sampTypeText.setText(jArr.getJSONObject(eventID).getString("type"));
                edited = true;
                sampTypeText.setFocusable(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sampNameText.addTextChangedListener(textWatcher);
        samplerNameText.addTextChangedListener(textWatcher);
        dateText.addTextChangedListener(textWatcher);
        sampTypeText.addTextChangedListener(textWatcher);

        if(edited) {
            button.setText("View Samples");
            button.setEnabled(true);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edited == false) {
                    sampName = sampNameText.getText().toString();
                    samplerName = samplerNameText.getText().toString();
                    date = dateText.getText().toString();
                    sampType = sampTypeText.getText().toString();

                    try {
                        jArr.getJSONObject(eventID).put("name", sampName);
                        jArr.getJSONObject(eventID).put("samplerName", samplerName);
                        jArr.getJSONObject(eventID).put("samplingDate", date);
                        jArr.getJSONObject(eventID).put("type", sampType);
                        if (!jArr.getJSONObject(eventID).has("samples") ) {
                            System.out.println("adding samples array");
                            jArr.getJSONObject(eventID).put("samples", new JSONArray());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //intent that passes ID to page where user can create samples
                    Intent intent = new Intent(MainActivity.this, samplesActivity.class);
                    intent.putExtra("eventID", eventID);
                    ((MyApplication) getApplication()).saveJson();
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(MainActivity.this, samplesActivity.class);
                    intent.putExtra("eventID", eventID);
                    ((MyApplication) getApplication()).saveJson();
                    startActivity(intent);
                }


            }
        });

        editButton = findViewById(R.id.mainEditButton);
        if(!edited) {
            editButton.setEnabled(false);
            editButton.setVisibility(View.INVISIBLE);
        }
        else {
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editButton.setText("Confirm");

                    sampNameText.setFocusable(true);
                    sampNameText.setFocusableInTouchMode(true);
                    samplerNameText.setFocusable(true);
                    samplerNameText.setFocusableInTouchMode(true);
                    dateText.setFocusable(true);
                    dateText.setFocusableInTouchMode(true);
                    sampTypeText.setFocusable(true);
                    sampTypeText.setFocusableInTouchMode(true);

                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sampName = sampNameText.getText().toString();
                            samplerName = samplerNameText.getText().toString();
                            date = dateText.getText().toString();
                            sampType = sampTypeText.getText().toString();

                            try {
                                jArr.getJSONObject(eventID).put("name", sampName);
                                jArr.getJSONObject(eventID).put("samplerName", samplerName);
                                jArr.getJSONObject(eventID).put("samplingDate", date);
                                jArr.getJSONObject(eventID).put("type", sampType);
                                jArr.getJSONObject(eventID).put("editDate", "placeholder");

                                if (!jArr.getJSONObject(eventID).has("samples") ) {
                                    System.out.println("adding samples array");
                                    jArr.getJSONObject(eventID).put("samples", new JSONArray());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(MainActivity.this, samplesActivity.class);
                            intent.putExtra("eventID", eventID);
                            ((MyApplication) getApplication()).saveJson();
                            startActivity(intent);
                        }
                    });

                }
            });
        }




    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            sampName = sampNameText.getText().toString();
            samplerName = samplerNameText.getText().toString();
            date = dateText.getText().toString();
            sampType = sampTypeText.getText().toString();


            button.setEnabled(!sampName.isEmpty() && !samplerName.isEmpty() && !date.isEmpty() && !sampType.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
