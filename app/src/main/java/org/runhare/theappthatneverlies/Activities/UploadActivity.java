package org.runhare.theappthatneverlies.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.runhare.theappthatneverlies.API.GrpcClient;
import org.runhare.theappthatneverlies.MainActivity;
import org.runhare.theappthatneverlies.R;
import org.runhare.theappthatneverlies.RouteCapture;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class UploadActivity extends AppCompatActivity {

    ArrayList<String> mobileArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        Intent uploadIntent = new Intent(this, UploadActivityPage.class);

        File routeDir = new File(getFilesDir().getPath() + "/Captures");

        List<File> files = null;

        if(routeDir.exists()) {
            files = Arrays.asList(Objects.requireNonNull(routeDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    Log.i("Tawanda", file.getName());
                    return file.getName().endsWith(".pb");
                }
            })));
        }

        if(files == null) {
            Toast.makeText(UploadActivity.this, "No captures yet", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        for (File file: files) {
            Log.i("Tawanda", file.getName());
            mobileArray.add(file.getName());
        }

        AtomicReference<FileInputStream> fileInputStream = new AtomicReference<>();
        AtomicReference<RouteCapture> routeCapture = new AtomicReference<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.list_view_item, mobileArray);

        ListView listView = findViewById(R.id.uploadList);
        listView.setAdapter(adapter);


        List<File> finalFiles = files;
        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            uploadIntent.putExtra("pbFilename", finalFiles.get(i).getName());
            startActivity(uploadIntent);

        }
        );
    }
}