package org.runhare.theappthatneverlies.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import org.runhare.theappthatneverlies.Models.CaptureViewModel;

import java.util.LinkedList;

public class LocationHandler {
    private LocationManager locationManager;
    private Location lastLocation = null;
    private Location currentLocation = null;

    public LinkedList<Location> getLocations() {
        return locations;
    }

    public void setLocations(LinkedList<Location> locations) {
        this.locations = locations;
    }

    private LinkedList<Location> locations;
    private float gpsAccuracy = 0.0f;

    public void setViewModel(CaptureViewModel viewModel) {
        this.viewModel = viewModel;
    }

    CaptureViewModel viewModel = null;

    @SuppressLint("MissingPermission")
    public LocationHandler(Context context) {


        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                5,
                locationListener
        );

        locations = new LinkedList<>();

        //Log.i("Tawanda", "GPS: " + lastLocation.getAccuracy());
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            currentLocation = location;
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            Log.i("Tawanda", msg);

            if(locations.size() != 0) {
                viewModel.setDistance(distanceFromLocation(locations.getLast(), location));
                viewModel.setGpsAccuracy(locations.getLast().getAccuracy());
                Log.i("Tawanda", "LocationPoints " + distanceFromLocation(locations.getLast(), location));
                Log.i("Tawanda", "GPS: " + location.getAccuracy());
            }

            locations.add(location);

        }
    };

    public CaptureViewModel getViewModel() {
        return viewModel;
    }

    @SuppressLint("MissingPermission")
    public Location getLastKnownLocation() {
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    @SuppressLint("MissingPermission")
    public Location getLastLocation() {

        if(locations.size() != 0) {
            lastLocation = locations.getLast();
        }


        return lastLocation;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public double distanceFromLocation(Location l1, Location l2) {

        LatLng ll1 = new LatLng(l1.getLatitude(), l1.getLongitude());
        LatLng ll2 = new LatLng(l2.getLatitude(), l2.getLongitude());

        return Math.round(LatLngTool.distance(ll1, ll2, LengthUnit.METER));
    }
}
