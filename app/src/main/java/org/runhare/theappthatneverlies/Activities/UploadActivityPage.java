package org.runhare.theappthatneverlies.Activities;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.runhare.theappthatneverlies.API.GrpcClient;
import org.runhare.theappthatneverlies.Models.CaptureViewModel;
import org.runhare.theappthatneverlies.Models.GrpcClientModel;
import org.runhare.theappthatneverlies.Models.GrpcClientViewModel;
import org.runhare.theappthatneverlies.R;
import org.runhare.theappthatneverlies.RouteCapture;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import io.grpc.stub.StreamObserver;


public class UploadActivityPage extends AppCompatActivity {

    private GrpcClient client;
    private GrpcClientViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_page);

        viewModel = new ViewModelProvider((ViewModelStoreOwner) this).get(GrpcClientViewModel.class);

        client = new GrpcClient("3.16.216.213", 50051);
        TextView routeName = findViewById(R.id.uploadPageRouteName);
        TextView uploadStatus = findViewById(R.id.progressStatus);
        Button uploadButton = findViewById(R.id.uploadPageButton);
        ProgressBar progressBar = findViewById(R.id.uploadProgressBar);
        TextView uploadComplete = findViewById(R.id.uploadComplete);
        TextView errorCode = findViewById(R.id.errorCode);
        
        Intent intent = getIntent();

        File capturesDir = new File(getFilesDir()+"/Captures");

        String fileName = intent.getStringExtra("pbFilename");

        File file = Objects.requireNonNull(capturesDir.listFiles(file1 -> file1.getName().equals(fileName)))[0];
        RouteCapture capture = null;
        
        try { 
            capture = RouteCapture.parseFrom(new FileInputStream(file));
            routeName.setText(capture.getRouteName());

        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadComplete.setVisibility(View.VISIBLE);
        uploadComplete.setText("Idle");


        RouteCapture finalCapture = capture;
        uploadButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                uploadComplete.setText("Uploading...");
                client.uploadRoute(finalCapture, new StreamObserver<RouteCapture>() {
                    @Override
                    public void onNext(RouteCapture value){
                        progressBar.setProgress(50);
                        viewModel.setIsComplete(false);
                    }

                    @Override
                    public void onError(Throwable t) {
                        progressBar.setProgress(0);
                        viewModel.setErrorMessage("Server error: " + t.getMessage());
                        viewModel.setProgress(progressBar.getProgress());
                        viewModel.setIsComplete(false);
                    }

                    @Override
                    public void onCompleted() {
                        progressBar.setProgress(100);
                        viewModel.setIsComplete(true);
                        viewModel.setProgress(progressBar.getProgress());
                    }
                });
            }
        });

        viewModel.getCapture().observe(this, new Observer<GrpcClientModel>() {
            @Override
            public void onChanged(GrpcClientModel grpcClientModel) {

                if(grpcClientModel.getErrorMessage() != null) {
                    errorCode.setVisibility(View.VISIBLE);
                    errorCode.setText(grpcClientModel.getErrorMessage());
                } else {
                    errorCode.setVisibility(View.INVISIBLE);
                    errorCode.setText("");
                }

                if(grpcClientModel.getProgress() > 0) {
                    uploadStatus.setText(grpcClientModel.getProgress()+"");
                }

                if(grpcClientModel.isComplete()) {

                    uploadComplete.setVisibility(View.VISIBLE);
                    uploadComplete.setText("success!");
                    uploadComplete.setTextColor(Color.GREEN);
                    uploadButton.setClickable(false);
                } else {
                    uploadComplete.setVisibility(View.VISIBLE);
                }

            }
        });



        Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();
    }
}