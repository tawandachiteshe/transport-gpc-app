package org.runhare.theappthatneverlies.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import org.runhare.theappthatneverlies.Activities.CaptureActivity;
import org.runhare.theappthatneverlies.MainActivity;

public class CaptureService extends Service {

    LocationManager locationManager;
    private Location lastLocation = null;

    public CaptureService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();


    }


    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.i("Tawanda","Locayion dsadsa");
        }
        else{
            Log.i("Tawanda","Locayion enabled");
        }
    }

}