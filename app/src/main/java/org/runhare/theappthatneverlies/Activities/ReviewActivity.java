package org.runhare.theappthatneverlies.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

public class ReviewActivity extends AppCompatActivity {

    ArrayList<String> mobileArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Intent mapIntent = new Intent(this, MapActivity.class);

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
            Toast.makeText(ReviewActivity.this, "No captures yet", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        for (File file: files) {
            Log.i("Tawanda", file.getName());
            mobileArray.add(file.getName());
        }

        FileInputStream fileInputStream = null;
        RouteCapture routeCapture = null;



        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.list_view_item, mobileArray);

        ListView listView = findViewById(R.id.captureList);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mapIntent.putExtra("routeFilename", mobileArray.get(i));
                Toast.makeText(ReviewActivity.this, "Item name: " + mobileArray.get(i), Toast.LENGTH_SHORT).show();
                startActivity(mapIntent);
            }
        });



    }
}