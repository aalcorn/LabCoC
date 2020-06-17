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

    TextView sampleLocText;
    TextView testCodeText;
    TextView faucetTypeText;
    TextView tempText;
    TextView bioCideText;
    TextView phText;
    TextView hotColdText;
    TextView volText;

    TextView faucetTypeTextView;
    TextView tempTextView;
    TextView bioCideTextView;
    TextView phTextView;
    TextView hotColdTextView;

    Button button;
    Button backButton;
    Button delButton;

    String hotCold;
    String vol;
    String sampleLoc;
    String faucetType;
    String temp;
    String biocide;
    String ph;
    String testCode;

    int eventID;
    int sID; //The place of the sample in the sampling event's sample array
    boolean edited = false;
    boolean isHPC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_edit);

        eventID = getIntent().getExtras().getInt("eventID");
        sID = getIntent().getExtras().getInt("sampleID") - 1;

        final JSONArray jArr = ((MyApplication) getApplication()).mainArray;
        System.out.println(jArr.toString());

        sampleLocText = findViewById(R.id.SampleLocText);
        hotColdText = findViewById(R.id.hotColdText);
        volText = findViewById(R.id.volText);
        testCodeText = findViewById(R.id.testCodeText);
        faucetTypeText = findViewById(R.id.faucetText);
        tempText = findViewById(R.id.tempText);
        bioCideText = findViewById(R.id.bioText);
        phText = findViewById(R.id.pHText);

        hotColdTextView = findViewById(R.id.hotColdTextView);
        faucetTypeTextView = findViewById(R.id.faucetTypeTextView);
        tempTextView = findViewById(R.id.tempTextView);
        bioCideTextView = findViewById(R.id.bioTextView);
        phTextView = findViewById(R.id.pHTextView);

        try {
            if ((jArr.getJSONObject(eventID).get("type").equals("hpc"))) {
                isHPC = true;

                hotColdText.setVisibility(View.INVISIBLE);
                faucetTypeText.setVisibility(View.INVISIBLE);
                tempText.setVisibility(View.INVISIBLE);
                bioCideText.setVisibility(View.INVISIBLE);
                phText.setVisibility(View.INVISIBLE);

                hotColdTextView.setVisibility(View.INVISIBLE);
                faucetTypeTextView.setVisibility(View.INVISIBLE);
                tempTextView.setVisibility(View.INVISIBLE);
                bioCideTextView.setVisibility(View.INVISIBLE);
                phTextView.setVisibility(View.INVISIBLE);

            }
            else {
                isHPC = false;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            //update fields pertaining to both legionella and HPC
            if(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("sampleLoc")) {
                sampleLocText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("sampleLoc"));
                edited = true;
                sampleLocText.setFocusable(false);
            }
            if(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("volume")) {
                volText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("volume"));
                edited = true;
                volText.setFocusable(false);
            }
            if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("testCode")) {
                testCodeText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("testCode"));
                edited = true;
                testCodeText.setFocusable(false);
            }
            //update legionella fields
            if (!isHPC) {
                if(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("hotCold")) {
                    hotColdText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("hotCold"));
                    edited = true;
                    hotColdText.setFocusable(false);
                }
                if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("faucetType")) {
                    faucetTypeText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("faucetType"));
                    edited = true;
                    faucetTypeText.setFocusable(false);
                }
                if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("temp")) {
                    tempText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("temp"));
                    edited = true;
                    tempText.setFocusable(false);
                }
                if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("biocide")) {
                    bioCideText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("biocide"));
                    edited = true;
                    bioCideText.setFocusable(false);
                }
                if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("pH")) {
                    phText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("pH"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sampleLocText.addTextChangedListener(textWatcher);
        testCodeText.addTextChangedListener(textWatcher);
        volText.addTextChangedListener(textWatcher);
        if(!isHPC) {
            hotColdText.addTextChangedListener(textWatcher);
            faucetTypeText.addTextChangedListener(textWatcher);
            tempText.addTextChangedListener(textWatcher);
            bioCideText.addTextChangedListener(textWatcher);
            phText.addTextChangedListener(textWatcher);
        }

        delButton = findViewById(R.id.sampleDeleteButton);
        button = findViewById(R.id.sampConfButton);
        button.setEnabled(false);

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

                    sampleLoc = sampleLocText.getText().toString();
                    vol = volText.getText().toString();
                    testCode = testCodeText.getText().toString();

                    Date dateObj = new Date();

                    try {
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleLoc", sampleLoc);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleID", sID);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("testCode", testCode);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("volume", vol);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleDate", dateObj);
                        if (!isHPC) {
                            hotCold = hotColdText.getText().toString();
                            faucetType = faucetTypeText.getText().toString();
                            temp = tempText.getText().toString();
                            biocide = bioCideText.getText().toString();
                            ph = phText.getText().toString();

                            jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("hotCold", hotCold);
                            jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("faucetType", faucetType);
                            jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("temp", temp);
                            jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("biocide", biocide);
                            jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("pH", ph);
                        }
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


                    sampleLocText.setFocusable(true);
                    sampleLocText.setFocusableInTouchMode(true);
                    volText.setFocusable(true);
                    volText.setFocusableInTouchMode(true);
                    testCodeText.setFocusableInTouchMode(true);
                    testCodeText.setFocusable(true);

                    if(!isHPC) {
                        hotColdText.setFocusable(true);
                        hotColdText.setFocusableInTouchMode(true);
                        faucetTypeText.setFocusable(true);
                        faucetTypeText.setFocusableInTouchMode(true);
                        tempText.setFocusable(true);
                        tempText.setFocusableInTouchMode(true);
                        bioCideText.setFocusableInTouchMode(true);
                        bioCideText.setFocusable(true);
                        phText.setFocusable(true);
                        phText.setFocusableInTouchMode(true);
                    }



                    //used for confirming data after edit button has been pressed.
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            sampleLoc = sampleLocText.getText().toString();
                            vol = volText.getText().toString();
                            testCode = testCodeText.getText().toString();

                            Date dateObj = new Date();

                            try {
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleLoc", sampleLoc);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleID", sID);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("testCode", testCode);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("volume", vol);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("editDate", dateObj);
                                if (!isHPC) {
                                    hotCold = hotColdText.getText().toString();
                                    faucetType = faucetTypeText.getText().toString();
                                    temp = tempText.getText().toString();
                                    biocide = bioCideText.getText().toString();
                                    ph = phText.getText().toString();

                                    jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("hotCold", hotCold);
                                    jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("faucetType", faucetType);
                                    jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("temp", temp);
                                    jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("biocide", biocide);
                                    jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("pH", ph);
                                }
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

        //sets the confirmation button clickable only if all fields are filled in
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //TODO add textchanged listners to all fields if legionella, only some if HPC
            sampleLoc = sampleLocText.getText().toString();
            vol = volText.getText().toString();
            testCode = testCodeText.getText().toString();

            if(!isHPC) {
                hotCold = hotColdText.getText().toString();
                faucetType = faucetTypeText.getText().toString();
                temp = tempText.getText().toString();
                biocide = bioCideText.getText().toString();
                ph = phText.getText().toString();

                button.setEnabled(!testCode.isEmpty() && !vol.isEmpty() && !sampleLoc.isEmpty() && !hotCold.isEmpty() && !faucetType.isEmpty() && !temp.isEmpty() && !biocide.isEmpty() && !ph.isEmpty());

            }
            else {
                button.setEnabled(!testCode.isEmpty() && !vol.isEmpty() && !sampleLoc.isEmpty());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void onBackPressed() {
        return;
    }
}
