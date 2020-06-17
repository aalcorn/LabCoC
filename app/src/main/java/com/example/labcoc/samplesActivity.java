package com.example.labcoc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class samplesActivity extends AppCompatActivity {

    private int eventID;
    private Button button;
    private int samplesCount;
    TextView eventNameText;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_samples);
        final JSONArray jArr = ((MyApplication) getApplication()).mainArray;

        eventID = getIntent().getExtras().getInt("eventID");

        eventNameText = findViewById(R.id.eventNameText);

        try {
            eventNameText.setText("Samples for Event: " + jArr.getJSONObject(eventID).getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            samplesCount = jArr.getJSONObject(eventID).getJSONArray("samples").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println(jArr.toString());

        //populate list with all previous samples (This will be done by going to the index of the sampling event and looking at the length of the samples array)

        for(int i = 1; i <= samplesCount; i++) {
            try {
                if(!jArr.getJSONObject(eventID).getJSONArray("samples").getJSONObject(i-1).has("deleted")) {
                    Button myButton = new Button(samplesActivity.this);
                    myButton.setText("Sample #"+ i);
                    myButton.setId(i);
                    final int id_ = myButton.getId();

                    LinearLayout layout = findViewById(R.id.scrollLayout);
                    layout.addView(myButton);

                    myButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Intent intent = new Intent(samplesActivity.this, sampleEditActivity.class);
                            intent.putExtra("eventID", eventID);
                            intent.putExtra("sampleID", id_);
                            ((MyApplication) getApplication()).saveJson();
                            startActivity(intent);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("ERROR");
            }

        }


        eventID = getIntent().getExtras().getInt("eventID");

        button = findViewById(R.id.createButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    jArr.getJSONObject(eventID).getJSONArray("samples").put(new JSONObject()); //instead, add new sample template to the obj
                    samplesCount = jArr.getJSONObject(eventID).getJSONArray("samples").length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Button myButton = new Button(samplesActivity.this);
                myButton.setText("Sample #"+ samplesCount);
                myButton.setId(samplesCount);
                final int id_ = myButton.getId();


                LinearLayout layout = findViewById(R.id.scrollLayout);
                layout.addView(myButton);

                final ScrollView scrollView = findViewById(R.id.scrollView);

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(scrollView.FOCUS_DOWN);
                    }
                });

                myButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent(samplesActivity.this, sampleEditActivity.class);
                        intent.putExtra("eventID", eventID);
                        intent.putExtra("sampleID", id_);
                        ((MyApplication) getApplication()).saveJson();
                        startActivity(intent);
                    }
                });

            }

        });


        backButton = findViewById(R.id.samplesBackButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(samplesActivity.this, selectActivity.class);
                ((MyApplication) getApplication()).saveJson();
                startActivity(intent);
            }
        });

    }

    public void onBackPressed() {
        return;
    }
}
