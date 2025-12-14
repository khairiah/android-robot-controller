package com.androiddeft.mdp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class CoordinatesSelectionService extends Activity   {
    private static final String TAG = "CoordinatesSelectionSee";

    private TextView xCoordTV;
    private TextView yCoordTV;
    SharedPreferences preferences;

    private Button wayPointBtn;
    private Button startCoordBtn;

    private int xCoord;
    private int yCoord;
    public Context context;
    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coordinates_selection_layout);











        setTitle("Select Coordinates");

        setResult(Activity.RESULT_CANCELED);

        xCoordTV = (TextView) findViewById(R.id.x_coordinate);
        yCoordTV = (TextView) findViewById(R.id.y_coordinate);


        wayPointBtn = (Button) findViewById(R.id.waypoint_btn);
        startCoordBtn = (Button) findViewById(R.id.start_coordinate_btn);

        xCoord = getIntent().getIntExtra("X", 0);
        yCoord = getIntent().getIntExtra("Y", 0);

        xCoordTV.setText(Integer.toString(xCoord));
        yCoordTV.setText(Integer.toString(yCoord));

        preferences = getSharedPreferences("MyCommandFile", Context.MODE_PRIVATE);



        wayPointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              /*  Intent messageIntent = new Intent(CoordinatesSelectionService.this, MainActivity.class);
                messageIntent.putExtra("X", xCoord);
                messageIntent.putExtra("Y", yCoord);
                messageIntent.putExtra("TYPE", "wayPoint");
              //  setResult(RESULT_OK, messageIntent);
                startActivity(messageIntent);
                finish();*/

                android.content.SharedPreferences.Editor editor = preferences.edit();

                editor.putString("key_cmd0", "wayPoint");
                editor.putString("key_cmd1", Integer.toString(xCoord));
                editor.putString("key_cmd2", Integer.toString(yCoord));
                editor.commit();
                finish();


            }
        });

        startCoordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent messageIntent = new Intent(CoordinatesSelectionService.this, MainActivity.class);
                messageIntent.putExtra("X", xCoord);
                messageIntent.putExtra("Y", yCoord);
                messageIntent.putExtra("TYPE", "startCoordinates");
              //  setResult(RESULT_OK, messageIntent);
                startActivity(messageIntent);
                finish();*/

                android.content.SharedPreferences.Editor editor = preferences.edit();

                editor.putString("key_cmd0", "StartPoints");
                editor.putString("key_cmd1", Integer.toString(xCoord));
                editor.putString("key_cmd2", Integer.toString(yCoord));
                editor.commit();
                finish();
            }
        });
    }



}
