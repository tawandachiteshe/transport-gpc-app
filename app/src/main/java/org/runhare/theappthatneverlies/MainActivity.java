package org.runhare.theappthatneverlies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.runhare.theappthatneverlies.Activities.CaptureActivity;
import org.runhare.theappthatneverlies.Activities.NewCaptureActivity;
import org.runhare.theappthatneverlies.Activities.ReviewActivity;

public class MainActivity extends AppCompatActivity {

    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(MainActivity.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(MainActivity.this, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton captureButton = findViewById(R.id.captureButton);
        ImageButton revivewButton = findViewById(R.id.reviewButton);
        ImageButton uploadButton = findViewById(R.id.uploadButton);
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 0);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                switch (view.getId()) {
                    
                    case R.id.captureButton:
                        SwitchActivity(NewCaptureActivity.class);
                        break;
                    case R.id.reviewButton:
                        checkPermission(Manifest.permission.READ_PHONE_STATE, 1);
                        SwitchActivity(ReviewActivity.class);
                        break;
                    case R.id.uploadButton:
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + view.getId());
                }
                
            }
        };

        captureButton.setOnClickListener(onClickListener);
        revivewButton.setOnClickListener(onClickListener);
        uploadButton.setOnClickListener(onClickListener);

    }

    private void SwitchActivity(Class<?> cls) {

        Intent captureIntent = new Intent(this, cls);
        startActivity(captureIntent);

    }

}