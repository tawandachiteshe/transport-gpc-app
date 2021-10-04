package org.runhare.theappthatneverlies.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.runhare.theappthatneverlies.Models.CaptureModel;
import org.runhare.theappthatneverlies.R;

public class NewCaptureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_capture);
        Intent captureIntent = new Intent(this, CaptureActivity.class);

        EditText routeName = findViewById(R.id.routeName);
        EditText description = findViewById(R.id.description);
        EditText vehicleType = findViewById(R.id.vehicleType);
        EditText vehicleCapacity = findViewById(R.id.vehicleCapacity);
        Button continueButton = findViewById(R.id.continueButton);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                captureIntent.putExtra("routeName", routeName.getText().toString());
                captureIntent.putExtra("description", description.getText().toString());
                captureIntent.putExtra("vehicleType", vehicleType.getText().toString());
                captureIntent.putExtra("vehicleCapacity", vehicleCapacity.getText().toString());

                startActivity(captureIntent);
            }
        });



    }
}