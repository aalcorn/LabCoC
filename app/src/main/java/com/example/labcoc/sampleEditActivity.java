package com.example.labcoc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

public class sampleEditActivity extends AppCompatActivity {

    TextView buildingText;
    TextView floorText;
    TextView roomText;

    TextView tempText;
    TextView bioCideText;
    TextView phText;

    TextView tempTextView;
    TextView bioCideTextView;
    TextView phTextView;
    TextView hotColdTextView;
    TextView faucetTypeTextView;

    TextView sampleNumberTextView;

    TextView timeCreated;
    TextView timeEdited;

    CheckBox potableBox;

    Button button;
    Button backButton;
    Button delButton;
    Button commentButton;

    String sampleLoc;
    String temp;
    String biocide;
    String ph;

    Spinner faucetTypeSpinner;
    Spinner hotColdSpinner;

    private int sampleNumber; //The label on the sampler's bottle
    int eventID;
    int sID; //The place of the sample in the sampling event's sample array
    boolean edited = false;
    boolean isLegionella;

    final String[] hotColdArray = {"Cold","Hot", "Mixed"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_sample_edit);

        eventID = getIntent().getExtras().getInt("eventID");
        sID = getIntent().getExtras().getInt("sampleID") - 1;

        final JSONArray jArr = ((MyApplication) getApplication()).mainArray;
        System.out.println(jArr.toString());

        sampleNumber = getIntent().getExtras().getInt("completedSamplesCount") + 1;

        buildingText = findViewById(R.id.buildingText);
        floorText = findViewById(R.id.floorText);
        roomText = findViewById(R.id.roomText);

        tempText = findViewById(R.id.tempText);
        bioCideText = findViewById(R.id.bioText);
        phText = findViewById(R.id.pHText);

        faucetTypeSpinner = findViewById(R.id.faucetSpinner);
        hotColdSpinner = findViewById(R.id.hotColdspinner);

        sampleNumberTextView = findViewById(R.id.sampleNumberTextView);

        potableBox = findViewById(R.id.potableBox);

        hotColdTextView = findViewById(R.id.hotColdTextView);
        faucetTypeTextView = findViewById(R.id.faucetTypeTextView);
        tempTextView = findViewById(R.id.tempTextView);
        bioCideTextView = findViewById(R.id.bioTextView);
        phTextView = findViewById(R.id.pHTextView);

        //Show views based on the type of sample
        try {
            if (!(jArr.getJSONObject(eventID).get("type").equals("Legionella"))) {
                isLegionella = false;

                hotColdSpinner.setVisibility(View.INVISIBLE);
                faucetTypeSpinner.setVisibility(View.INVISIBLE);
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
                isLegionella = true;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //SPINNER
        final String[] faucetArray = {"Sink","Tub", "Shower", "Therapy Tub", "Therapy Pool", "Emergency Eyewash", "Emergency Shower", "Drinking Fountain", "Sink Sprayer", "Ice Machine"
        , "Cooling tower", "Hot Water Storage Tank", "Water Tower", "Fire Sprinkler System", "Outdoor Water Feature", "Facility Main", "Building Incoming Water", "Other"};
        ArrayAdapter<String> faucetAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, faucetArray);
        faucetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        faucetTypeSpinner.setAdapter(faucetAdapter);

        //final String[] hotColdArray = {"Cold","Hot", "Mixed"};
        final ArrayAdapter<String> hotColdAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hotColdArray);
        hotColdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hotColdSpinner.setAdapter(hotColdAdapter);

        hotColdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkInputValidity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Comment button setup
        commentButton = findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(sampleEditActivity.this);
                dialog.setTitle("Comment: ");

                final EditText commentText = new EditText(sampleEditActivity.this);
                try {
                    if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("comment")) {
                        commentText.setText(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("comment"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.setView(commentText);

                dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("comment", commentText.getText().toString());
                            ((MyApplication) getApplication()).saveJson();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                dialog.setNegativeButton("Cancel", null).show();
            }
        });

        //Delete button setup
        delButton = findViewById(R.id.sampleDeleteButton);
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

        try {
            //update fields pertaining to both legionella and HPC
            if(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("sampleLocation")) {
                if (!jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("sampleLocation").equals("")) {
                    String[] locationArray = jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getString("sampleLocation").split(" ");
                    buildingText.setText(locationArray[1]);
                    floorText.setText(locationArray[2]);
                    roomText.setText(locationArray[5]);

                    buildingText.setFocusable(false);
                    floorText.setFocusable(false);
                    roomText.setFocusable(false);

                }
            }
            if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("potable")) {
                if(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).getBoolean("potable")) {
                    potableBox.setChecked(true);
                }
                edited = true;
                potableBox.setEnabled(false);
            }
            if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("sampleNumber")) {
                sampleNumberTextView.setText("Sample Number: " + jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).get("sampleNumber"));
            }
            else {
                sampleNumberTextView.setText("Sample Number: " + sampleNumber);
            }
            //update legionella fields
            if (isLegionella) {
                if(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("hotCold")) {
                    for(int i = 0; i < hotColdArray.length; i++) {
                        if(hotColdArray[i].equals(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).get("hotCold"))) {
                            hotColdSpinner.setSelection(i);
                            hotColdSpinner.setEnabled(false);
                            edited = true;
                        }
                    }
                }
                if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("faucetType")) {
                    for(int i = 0; i < faucetArray.length; i++) {
                        if(faucetArray[i].equals(jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).get("faucetType"))) {
                            faucetTypeSpinner.setSelection(i);
                            faucetTypeSpinner.setEnabled(false);
                            edited = true;
                        }
                    }
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
                    edited = true;
                    phText.setFocusable(false);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        buildingText.addTextChangedListener(textWatcher);
        floorText.addTextChangedListener(textWatcher);
        roomText.addTextChangedListener(textWatcher);
        if(isLegionella) {
            tempText.addTextChangedListener(textWatcher);
            bioCideText.addTextChangedListener(textWatcher);
            phText.addTextChangedListener(textWatcher);
        }

        button = findViewById(R.id.sampConfButton);
        button.setEnabled(false);
        try {
            if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("sampleLocation") && !edited) {
                button.setEnabled(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(edited) {
            button.setText("EDIT");
            button.setEnabled(true);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add sample params to array and go back to sampling event page corresponding to this sample
                if(!edited) {
                    sampleLoc = ("Building " + buildingText.getText().toString() + " " + floorText.getText().toString() + " Floor Room " + roomText.getText().toString());

                    Date dateObj = new Date();

                    try {
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleLocation", sampleLoc);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleID", sID);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("time", dateObj);
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("potable", potableBox.isChecked());
                        jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleNumber", sampleNumber);
                        if (isLegionella) {
                            temp = tempText.getText().toString();
                            biocide = bioCideText.getText().toString();
                            ph = phText.getText().toString();

                            jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("hotCold", hotColdArray[hotColdSpinner.getSelectedItemPosition()]);
                            jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("faucetType", faucetArray[faucetTypeSpinner.getSelectedItemPosition()]);
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


                    buildingText.setFocusable(true);
                    buildingText.setFocusableInTouchMode(true);
                    floorText.setFocusable(true);
                    floorText.setFocusableInTouchMode(true);
                    roomText.setFocusable(true);
                    roomText.setFocusableInTouchMode(true);
                    potableBox.setEnabled(true);

                    if(isLegionella) {
                        tempText.setFocusable(true);
                        tempText.setFocusableInTouchMode(true);
                        bioCideText.setFocusableInTouchMode(true);
                        bioCideText.setFocusable(true);
                        phText.setFocusable(true);
                        phText.setFocusableInTouchMode(true);

                        faucetTypeSpinner.setEnabled(true);
                        hotColdSpinner.setEnabled(true);
                    }



                    //used for confirming data after edit button has been pressed.
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sampleLoc = ("Building " + buildingText.getText().toString() + " " + floorText.getText().toString() + " Floor Room " + roomText.getText().toString());

                            Date dateObj = new Date();

                            try {
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleLocation", sampleLoc);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("sampleID", sID);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("editTime", dateObj);
                                jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("potable", potableBox.isChecked());
                                if (isLegionella) {
                                    temp = tempText.getText().toString();
                                    biocide = bioCideText.getText().toString();
                                    ph = phText.getText().toString();

                                    jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("hotCold", hotColdArray[hotColdSpinner.getSelectedItemPosition()]);
                                    jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).put("faucetType", faucetArray[faucetTypeSpinner.getSelectedItemPosition()]);
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

        timeCreated = findViewById(R.id.timeCreatedView);
        timeEdited = findViewById(R.id.editedView);

        try {
            if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("time")) {
                timeCreated.setText("Time created: " + jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).get("time"));
            }
            if (jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).has("editTime")) {
                timeEdited.setText("Time last edited: " + jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(sID).get("editTime"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        //sets the confirmation button clickable only if all fields are filled in
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String building = buildingText.getText().toString();
            String floor = floorText.getText().toString();
            String room = roomText.getText().toString();

            if(isLegionella) {
                temp = tempText.getText().toString();
                biocide = bioCideText.getText().toString();
                ph = phText.getText().toString();

                button.setEnabled(!temp.isEmpty() && !biocide.isEmpty() && !ph.isEmpty() && !building.isEmpty() && !floor.isEmpty() && !room.isEmpty());

            }
            else {
                button.setEnabled(!building.isEmpty() && !floor.isEmpty() && !room.isEmpty());
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            checkInputValidity();
        }
    };

    public void checkInputValidity() {
        if (!phText.getText().toString().isEmpty() && !phText.getText().toString().equals(".")) {
            Double phDub = Double.parseDouble(phText.getText().toString());
            if (phDub < 0 || phDub > 14) {
                phText.setTextColor(Color.RED);
            }
            else {
                phText.setTextColor(Color.BLACK);
            }

        }

        if (!tempText.getText().toString().isEmpty() && !tempText.getText().toString().equals(".")) {
            String hotColdString = hotColdArray[hotColdSpinner.getSelectedItemPosition()];
            Double tempDub = Double.parseDouble(tempText.getText().toString());
            switch (hotColdString) {
                case "Cold":
                    if(tempDub > 85 || tempDub < 65) {
                        tempText.setTextColor(Color.RED);
                    }
                    else {
                        tempText.setTextColor(Color.BLACK);
                    }
                    break;
                case "Hot":
                    if (tempDub > 110 || tempDub < 87) {
                        tempText.setTextColor(Color.RED);
                    }
                    else {
                        tempText.setTextColor(Color.BLACK);
                    }
                    break;
                case "Mixed":
                    if (tempDub > 110 || tempDub < 65) {
                        tempText.setTextColor(Color.RED);
                    }
                    else {
                        tempText.setTextColor(Color.BLACK);
                    }
                    break;
            }
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(sampleEditActivity.this, samplesActivity.class);
        intent.putExtra("eventID", eventID);
        ((MyApplication) getApplication()).saveJson();
        startActivity(intent);
    }
}
