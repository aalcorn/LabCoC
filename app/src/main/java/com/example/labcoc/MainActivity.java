package com.example.labcoc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public String sampName;
    public String samplerName;
    public String date;
    public String sampType;
    public String facilityID;
    private int eventID;

    TextView sampNameText;
    TextView samplerNameText;

    TextView createdText;
    TextView editedText;

    //ImageView imageView;

    RadioButton hpcButton;
    RadioButton legionellaButton;

    Button button;
    Button editButton;
    Button delButton;

    Spinner spinner;

    boolean edited = false;
    boolean complete = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventID = getIntent().getExtras().getInt("eventID") - 1;

        final JSONArray jArr = ((MyApplication) getApplication()).mainArray;
        final JSONArray fArr = ((MyApplication) getApplication()).facilitiesArray;

        hpcButton = findViewById(R.id.hpcButton);
        legionellaButton = findViewById(R.id.legionButton);
        sampNameText = findViewById(R.id.nameBox);
        samplerNameText = findViewById(R.id.samplerNameBox);
        button = findViewById(R.id.downSampButton);
        button.setEnabled(false);
        delButton = findViewById(R.id.mainDelButton);
        delButton.setEnabled(false);
        delButton.setVisibility(View.INVISIBLE);

        try {
            if(jArr.getJSONObject(eventID).has("completed")) {
                complete = jArr.getJSONObject(eventID).getBoolean("completed");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (complete) {
            System.out.println("Complete!");
        }
        //SPINNER

        //put all facility names in an array. When a facility is selected, update a new facilityID variable with the facility's corresponding ID.
        String[] array = new String[fArr.length()];
        for(int i = 0; i < fArr.length(); i++) {
            try {
                array[i] = fArr.getJSONObject(i).getString("name");
            } catch (JSONException e) {
                System.out.println("This is the error: ");
                e.printStackTrace();
            }
        }

        spinner = findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Drawable d = spinner.getBackground().getCurrent();

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

            if(jArr.getJSONObject(eventID).has("type")) {
                if(jArr.getJSONObject(eventID).get("type").equals("hpc")) {
                    hpcButton.setChecked(true);
                    legionellaButton.setEnabled(false);
                }
                else {
                    legionellaButton.setChecked(true);
                    hpcButton.setEnabled(false);
                }
                edited = true;
            }

            if (jArr.getJSONObject(eventID).has("facilityID")) {
                for(int i = 0; i < fArr.length(); i++) {
                    System.out.println(jArr.getJSONObject(eventID).getString("facilityID") + " Fac ID JSON");
                    System.out.println(fArr.getJSONObject(i).getString("_id") + " Fac ID facArray");
                    System.out.println("Loop");
                    if(jArr.getJSONObject(eventID).getString("facilityID").equals(fArr.getJSONObject(i).getString("_id"))) {
                        spinner.setSelection(i);
                        spinner.setEnabled(false);
                        spinner.setBackgroundDrawable(d);
                        edited = true;
                        System.out.println(jArr.getJSONObject(eventID).getString("facilityID") + " if statement");
                    }
                }
            }
            /*if(jArr.getJSONObject(eventID).has("signature")) {
                imageView = findViewById(R.id.imageView);
                Bitmap bitmap = StringToBitMap(jArr.getJSONObject(eventID).getString("signature"));
                imageView.setBackgroundColor(Color.parseColor("#d6d6d6"));
                imageView.setImageBitmap(bitmap);
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sampNameText.addTextChangedListener(textWatcher);
        samplerNameText.addTextChangedListener(textWatcher);


        if(edited && complete) {
            button.setText("View Samples");
            button.setEnabled(true);
        }

        // If the event has not been edited, this will be the confirm button. If it has been edited, it will be the view samples button. If "complete" is false, it overrides the edited field
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(!edited || !complete) {
                        button.setEnabled(true);
                        sampName = sampNameText.getText().toString();
                        samplerName = samplerNameText.getText().toString();
                        spinner.setEnabled(true);
                        System.out.println("Position: " + spinner.getSelectedItemPosition());
                        System.out.println("fArr: " + fArr.toString());
                        try {
                            facilityID = fArr.getJSONObject(spinner.getSelectedItemPosition()).getString("_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (hpcButton.isChecked()) {
                            sampType = "hpc";
                        }
                        else {
                            sampType = "legionella";
                        }

                        Date dateobj = new Date();

                        try {
                            jArr.getJSONObject(eventID).put("name", sampName);
                            jArr.getJSONObject(eventID).put("samplerName", samplerName);
                            jArr.getJSONObject(eventID).put("samplingDate", dateobj);
                            jArr.getJSONObject(eventID).put("type", sampType);
                            jArr.getJSONObject(eventID).put("facilityID", facilityID);
                            jArr.getJSONObject(eventID).put("completed", true);
                            if (!jArr.getJSONObject(eventID).has("samples") ) {
                                System.out.println("adding samples array");
                                jArr.getJSONObject(eventID).put("samples", new JSONArray());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(facilityID);
                        //intent that passes ID to page where user can create samples
                        Intent intent = new Intent(MainActivity.this, samplesActivity.class);
                        intent.putExtra("eventID", eventID);
                        ((MyApplication) getApplication()).saveJson();
                        startActivity(intent);
                    }
                    else {
                        //Says view samples, takes them to see the samples for that event
                        Intent intent = new Intent(MainActivity.this, samplesActivity.class);
                        intent.putExtra("eventID", eventID);
                        ((MyApplication) getApplication()).saveJson();
                        startActivity(intent);
                    }
            }
        });

        editButton = findViewById(R.id.mainEditButton);
        if(!edited || !complete) {
            editButton.setEnabled(false);
            editButton.setVisibility(View.INVISIBLE);
        }
            //If sample has been edited and edit button is clicked, show delete button and disable "view samples" button so the user doesn't accidentally hit it thinking they're confirming the edit
            else {
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button.setEnabled(false);
                        delButton.setEnabled(true);
                        delButton.setVisibility(View.VISIBLE);

                        delButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //pop up a window that asks if they're sure they want to delete; if they are, add delete flag to the sample
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("")
                                        .setMessage("Delete this Event?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(MainActivity.this,"Deleted", Toast.LENGTH_LONG).show();
                                                //jArr.getJSONObject(eventID).put("deleted", true);
                                                jArr.remove(eventID);
                                                Intent intent = new Intent(MainActivity.this, selectActivity.class);
                                                ((MyApplication) getApplication()).saveJson();
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null).show();
                            }
                        });

                        editButton.setText("Confirm");

                        sampNameText.setFocusable(true);
                        sampNameText.setFocusableInTouchMode(true);
                        samplerNameText.setFocusable(true);
                        samplerNameText.setFocusableInTouchMode(true);
                        hpcButton.setEnabled(true);
                        legionellaButton.setEnabled(true);
                        spinner.setEnabled(true);

                        editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sampName = sampNameText.getText().toString();
                                samplerName = samplerNameText.getText().toString();
                                spinner.setEnabled(true);
                                try {
                                    facilityID = fArr.getJSONObject(spinner.getSelectedItemPosition()).getString("_id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (hpcButton.isChecked()) {
                                    sampType = "hpc";
                                }
                                else {
                                    sampType = "legionella";
                                }

                                Date dateobj = new Date();

                                try {
                                    jArr.getJSONObject(eventID).put("name", sampName);
                                    jArr.getJSONObject(eventID).put("samplerName", samplerName);
                                    jArr.getJSONObject(eventID).put("type", sampType);
                                    jArr.getJSONObject(eventID).put("editDate", dateobj);
                                    jArr.getJSONObject(eventID).put("facilityID", facilityID);
                                    if (!jArr.getJSONObject(eventID).has("samples") ) {
                                        System.out.println("adding samples array");
                                        jArr.getJSONObject(eventID).put("samples", new JSONArray());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(facilityID);
                                Intent intent = new Intent(MainActivity.this, samplesActivity.class);
                                intent.putExtra("eventID", eventID);
                                ((MyApplication) getApplication()).saveJson();
                                startActivity(intent);
                            }
                        });

                        button.setEnabled(false);

                    }
                });


            }

        hpcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sampName = sampNameText.getText().toString();
                samplerName = samplerNameText.getText().toString();

                System.out.println(hpcButton.isChecked());
                System.out.println(legionellaButton.isChecked());

                button.setEnabled(!sampName.isEmpty() && !samplerName.isEmpty() && (hpcButton.isChecked() || legionellaButton.isChecked()));
            }
        });

        legionellaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sampName = sampNameText.getText().toString();
                samplerName = samplerNameText.getText().toString();

                System.out.println(hpcButton.isChecked());
                System.out.println(legionellaButton.isChecked());

                button.setEnabled(!sampName.isEmpty() && !samplerName.isEmpty() && (hpcButton.isChecked() || legionellaButton.isChecked()));
            }
        });


        createdText = findViewById(R.id.timeEditedText);
        editedText = findViewById(R.id.timeCreatedTextView);

        try {
            if (jArr.getJSONObject(eventID).has("samplingDate")) {
                createdText.setText("Time created: " + jArr.getJSONObject(eventID).get("samplingDate"));
            }
            if (jArr.getJSONObject(eventID).has("editDate")) {
                editedText.setText("Time last edited: " + jArr.getJSONObject(eventID).get("editDate"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

            if(!edited || !complete) {
                button.setEnabled(!sampName.isEmpty() && !samplerName.isEmpty() && (hpcButton.isChecked() || legionellaButton.isChecked()));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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

    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this, selectActivity.class);
        startActivity(intent);
    }

}
