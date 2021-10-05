package org.runhare.theappthatneverlies.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.runhare.theappthatneverlies.Datastore.MyDatastore;
import org.runhare.theappthatneverlies.MainActivity;
import org.runhare.theappthatneverlies.Models.CaptureModel;
import org.runhare.theappthatneverlies.Models.CaptureViewModel;
import org.runhare.theappthatneverlies.R;
import org.runhare.theappthatneverlies.RouteCapture;
import org.runhare.theappthatneverlies.Services.CaptureService;
import org.runhare.theappthatneverlies.Utils.LocationHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class CaptureActivity extends AppCompatActivity {

    private boolean toggleCapture = false;
    private Intent serviceIntent = null;
    private Intent newCaptureIntent = null;
    private LocationHandler locationHandler = null;
    private int numOfStops = 0;
    private int numberOfPassengers = 0;
    private ArrayList<Location> stopsList;
    private CaptureModel captureModel;
    TextView gpsIndicator;
    TextView distanceText;
    private CaptureViewModel viewModel = null;
    private MyDatastore myDatastore = null;
    private RouteCapture.Builder routeCaptureBuilder;
    private Observable<RouteCapture.Builder> builderObservable;
    SharedPreferences sharedpreferences;
    final String MyPREFERENCES = "AppData";

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    public void saveCaptureToStorage(RouteCapture.Builder capture) {

        builderObservable = Observable.create(new ObservableOnSubscribe<RouteCapture.Builder>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<RouteCapture.Builder> emitter) throws Throwable {
                emitter.onNext(capture);
                emitter.onComplete();
            }
        });

        builderObservable.subscribe(new Observer<RouteCapture.Builder>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(RouteCapture.@NonNull Builder builder) {
                RouteCapture routeCapture = builder.build();
                File file = getFilesDir();

                File captureDir = new File(file.getPath()+"/Captures");

                if(!captureDir.exists()) {
                    boolean success = captureDir.mkdir();
                }

                int routeIndex = sharedpreferences.getInt("routeIndex", 0);
                FileOutputStream outputStream = null;

                try {
                    outputStream = new FileOutputStream( captureDir+ "/route_"+routeIndex+".pb");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    routeCapture.writeTo(outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                Toast.makeText(CaptureActivity.this, "Capture saved!", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void initializeLocationHandler() {
        locationHandler = new LocationHandler(this);
        locationHandler.setViewModel(viewModel);
        locationHandler.getViewModel().setCaptureModel(captureModel);
        locationHandler.getViewModel().getCapture().observe(this, captureModel -> {

            double distance = (captureModel.getDistance() / 1000.0);
            distanceText.setText(distance + "KM");
            gpsIndicator.setText("GPS Accuracy: " + captureModel.getGpsAccuracy());

        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();


        routeCaptureBuilder = RouteCapture.newBuilder();

        viewModel = new ViewModelProvider((ViewModelStoreOwner) this).get(CaptureViewModel.class);

        captureModel = new CaptureModel();

        stopsList = new ArrayList<>();
        serviceIntent = new Intent(this, CaptureService.class);
        newCaptureIntent = new Intent(this, NewCaptureActivity.class);

        Intent intent = getIntent();

        String routeName = intent.getStringExtra("routeName");
        String description = intent.getStringExtra("description");
        String vehicleType = intent.getStringExtra("vehicleType");
        int vehicleCapacity = Integer.parseInt(intent.getStringExtra("vehicleCapacity"));

        captureModel.setRouteName(routeName);
        captureModel.setDescription(description);
        captureModel.setVehicleCapacity(vehicleCapacity);
        captureModel.setVehicleType(vehicleType);

        routeCaptureBuilder.setRouteName(routeName);
        routeCaptureBuilder.setDescription(description);
        routeCaptureBuilder.setVehicleCapacity(vehicleCapacity);
        routeCaptureBuilder.setVehicleType(vehicleType);



        ImageButton startCapture = findViewById(R.id.startCaptureButton);
        TextView captureStatus = findViewById(R.id.captureStatus);
        TextView routeNameDisplay = findViewById(R.id.routeNameDisplay);
        FloatingActionButton addStop = findViewById(R.id.addStop);
        gpsIndicator = findViewById(R.id.gpsStateindicator);
        distanceText = findViewById(R.id.distanceId);
        Chronometer duration = findViewById(R.id.durationID);
        TextView stops = findViewById(R.id.stopsId);
        FloatingActionButton addPassenger = findViewById(R.id.addPassenger);
        FloatingActionButton subtractPassenger = findViewById(R.id.subtractPassenger);
        TextView passengerCounter = findViewById(R.id.passengerCounter);

        routeNameDisplay.setText(routeName);

        duration.setBase(SystemClock.elapsedRealtime());

        View.OnClickListener passengerListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (toggleCapture) {

                    switch (view.getId()) {
                        case R.id.addPassenger:
                            numberOfPassengers++;
                            break;
                        case R.id.subtractPassenger:
                            if (numberOfPassengers <= 0) {
                                numberOfPassengers = 0;
                                return;
                            }
                            numberOfPassengers--;
                            break;
                    }

                } else {
                    Toast.makeText(CaptureActivity.this, "Please start capture to add/subtract passengers"
                            , Toast.LENGTH_SHORT).show();
                }

                if (numberOfPassengers < 10) {
                    passengerCounter.setText("00" + numberOfPassengers);
                } else if (numberOfPassengers < 100) {
                    passengerCounter.setText("0" + numberOfPassengers);
                } else {
                    passengerCounter.setText("" + numberOfPassengers);
                }
            }
        };

        addPassenger.setOnClickListener(passengerListener);
        subtractPassenger.setOnClickListener(passengerListener);


        addStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (toggleCapture) {
                    numOfStops++;

                    if (locationHandler.getCurrentLocation() != null) {
                        stopsList.add(locationHandler.getCurrentLocation());
                    }

                    for (Location location : stopsList) {
                        Log.i("Tawanda", location.toString());
                    }


                    if (numOfStops < 10) {
                        stops.setText("00" + numOfStops);
                    } else if (numOfStops < 100) {
                        stops.setText("0" + numOfStops);
                    } else {
                        stops.setText("" + numOfStops);
                    }
                } else {
                    Toast.makeText(CaptureActivity.this, "Please start capture to add stops"
                            , Toast.LENGTH_SHORT).show();
                }

            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View view) {
                toggleCapture = !toggleCapture;

                if (toggleCapture) {

                    startCapture.setImageResource(R.drawable.outline_stop_circle_black_48);
                    duration.setBase(SystemClock.elapsedRealtime());
                    duration.start();
                    captureStatus.setText("Active");
                    initializeLocationHandler();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(CaptureActivity.this);
                    builder.setMessage("Are you sure? You want to stop capture")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                startCapture.setImageResource(R.drawable.outline_play_circle_filled_black_48);
                                duration.setBase(SystemClock.elapsedRealtime());
                                duration.stop();
                                captureStatus.setText("Inactive");

                                if(locationHandler.getLocations().size() != 0) {
                                    for (Location location: locationHandler.getLocations()) {
                                        routeCaptureBuilder.addPoints(RouteCapture.Location.newBuilder()
                                                .setLatitude(location.getLatitude()).setLongitude(location.getLongitude()).build());
                                    }
                                }

                                if(stopsList.size() != 0) {
                                    for (Location location : stopsList) {
                                        routeCaptureBuilder.addStops(RouteCapture.Location.newBuilder()
                                                .setLatitude(location.getLatitude()).setLongitude(location.getLongitude()).build());
                                    }
                                    if(!sharedpreferences.contains("routeIndex")) {
                                        editor.putInt("routeIndex", 0);
                                        editor.commit();
                                    } else {
                                        editor.putInt("routeIndex", sharedpreferences.getInt("routeIndex", 0) + 1);
                                        editor.commit();
                                    }
                                    routeCaptureBuilder.setDuration(duration.getBase());
                                    saveCaptureToStorage(routeCaptureBuilder);
                                    startActivity(new Intent(CaptureActivity.this, MainActivity.class));
                                } else {
                                    Toast.makeText(CaptureActivity.this, "Capture did not save " +
                                            "no stops where added", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(CaptureActivity.this, MainActivity.class));
                                }


                            }).setNegativeButton("No", (dialogInterface, i) -> {

                            }).show();

                }

            }
        };

        startCapture.setOnClickListener(onClickListener);



    }
}