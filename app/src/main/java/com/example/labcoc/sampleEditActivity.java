package com.example.labcoc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

public class sampleEditActivity extends AppCompatActivity {

    TextView sampleIdText;
    TextView hotColdText;
    TextView volText;
    Button button;
    Button backButton;
    Button delButton;

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

        sampleIdText = findViewById(R.id.SampleLocText);
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

        delButton = findViewById(R.id.sampleDeleteButton);
        button = findViewById(R.id.sampConfButton);

        delButton.setVisibility(View.INVISIBLE);
        delButton.setEnabled(false);

        if(edited) {
            button.setText("EDIT");
            button.setEnabled(true);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add sample params to array and go back to sampling event page corresponding to this sample

                if(!edited) {

                    hotCold = hotColdText.getText().toString();
                    vol = volText.getText().toString();

                    Date dateObj = new Date();

                    try {
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleID", sID);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("temp", hotCold);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("volume", vol);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleDate", dateObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    System.out.println(jArr.toString());
                    Intent intent = new Intent(sampleEditActivity.this, samplesActivity.class);
                    intent.putExtra("eventID", eventID);
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


                            hotCold = hotColdText.getText().toString();
                            vol = volText.getText().toString();

                            Date dateobj = new Date();

                            try {
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleID", sID);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("temp", hotCold);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("volume", vol);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("editDate", dateobj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            System.out.println(jArr.toString());
                            Intent intent = new Intent(sampleEditActivity.this, samplesActivity.class);
                            intent.putExtra("eventID", eventID);
                            ((MyApplication) getApplication()).saveJson();
                            startActivity(intent);

                        }
                    });

                    //activation of delete button here
                    delButton.setEnabled(true);
                    delButton.setVisibility(View.VISIBLE);

                    delButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //pop up a window that asks if they're sure they want to delete; if they are, add delete flag to the sample
                            new AlertDialog.Builder(sampleEditActivity.this)
                                    .setTitle("")
                                    .setMessage("Delete this sample?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(sampleEditActivity.this,"Deleted", Toast.LENGTH_LONG).show();
                                            try {
                                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("deleted", true);
                                                Intent intent = new Intent(sampleEditActivity.this, samplesActivity.class);
                                                intent.putExtra("eventID", eventID);
                                                ((MyApplication) getApplication()).saveJson();
                                                startActivity(intent);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })

                                    .setNegativeButton(android.R.string.no, null).show();
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
            hotCold = hotColdText.getText().toString();
            vol = volText.getText().toString();

            button.setEnabled(!hotCold.isEmpty() && !vol.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
