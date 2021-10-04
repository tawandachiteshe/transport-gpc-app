package org.runhare.theappthatneverlies.Activities;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;

import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONObject;
import org.runhare.theappthatneverlies.R;
import org.runhare.theappthatneverlies.RouteCapture;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MapActivity extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {

    private MapView mapbox = null;
    private int REQUEST_CHECK_SETTINGS = 1;
    private SettingsClient settingsClient = null;

    //2
    private MapboxMap map = null;
    private PermissionsManager permissionManager = null;
    private Location originLocation = null;

    private LocationEngine locationEngine = null;
    private LocationComponent locationComponent = null;

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private com.mapbox.geojson.Point origin;
    private com.mapbox.geojson.Point destination;
    private Intent mapIntent = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getResources().getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_map);

        mapIntent = getIntent();

        mapbox = (MapView) findViewById(R.id.mainMapView);

        if (savedInstanceState != null) {
            mapbox.onCreate(savedInstanceState);
        }

        mapbox.getMapAsync(this);
        settingsClient = LocationServices.getSettingsClient(this);

        checkPermission(Manifest.permission.READ_PHONE_STATE, 1);
    }



    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MapActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MapActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == Activity.RESULT_CANCELED) {
                enableLocation();
            } else if (requestCode == Activity.RESULT_CANCELED) {
                finish();
            }

        }
    }

    private void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
        } else {
            permissionManager = new PermissionsManager(this);
            permissionManager.requestLocationPermissions(this);
        }
    }



    private void setCameraPosition(Location location) {

        if(map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                    location.getLongitude()), 12.0));

            map.addMarker(new MarkerOptions().position(new LatLng(new LatLng(location.getLatitude(), location.getLongitude()))));
        }

    }

    public MapActivity() {
        super();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStart() {
        super.onStart();
        mapbox.onStart();

        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            if(locationEngine != null) {
                locationEngine.requestLocationUpdates(new LocationEngineRequest.Builder(5000).build(),
                        PendingIntent.getActivity(this, REQUEST_CHECK_SETTINGS, null,
                        PendingIntent.FLAG_MUTABLE));
            }

            if (locationComponent != null) {
                locationComponent.onStart();
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        //locationEngine.removeLocationUpdates();
        if(locationComponent != null)
            locationComponent.onStop();

        mapbox.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapbox.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapbox.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapbox.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapbox.onResume();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapbox.onSaveInstanceState(outState);

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "This app needs location permission to be able to show your location on the map", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        } else {
            Toast.makeText(this, "User location was not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        Log.i("Tawanda", "MapReady");
        map = mapboxMap;
       LocationSettingsRequest.Builder locationRequestSettings = new
               LocationSettingsRequest.Builder().addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));

        LocationSettingsRequest locationRequest = locationRequestSettings.build();
        settingsClient.checkLocationSettings(locationRequest).addOnSuccessListener(locationSettingsResponse -> enableLocation()).addOnFailureListener(e -> {
            int statusCode = ((ApiException)e).getStatusCode();

            if(statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                try {
                    resolvableApiException.startResolutionForResult(MapActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });

        if(map != null) {

            File capturesDir = new File(getFilesDir()+"/Captures");

            File file = Objects.requireNonNull(capturesDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getName().equals(mapIntent.getStringExtra("routeFilename"));
                }
            }))[0];

            FileInputStream fileInputStream;
            RouteCapture routeCapture = null;

            try {
                fileInputStream = new FileInputStream(file);
                routeCapture = RouteCapture.parseFrom(fileInputStream);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            for(RouteCapture.Location stops: routeCapture.getStopsList()) {
                Location location = new Location("");
                location.setLatitude(stops.getLatitude());
                location.setLongitude(stops.getLongitude());
                setCameraPosition(location);
                map.addMarker(new MarkerOptions().position(new LatLng(stops.getLatitude(), stops.getLongitude())));
            }


            RouteCapture finalRc = routeCapture;

            mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    double lng = finalRc.getStopsList().get(0).getLongitude();
                    double lat = finalRc.getStopsList().get(0).getLatitude();
                    int lastIndex =  finalRc.getStopsList().size() - 1;
                    double dstlng = finalRc.getStopsList().get(lastIndex).getLongitude();
                    double dstlat = finalRc.getStopsList().get(lastIndex).getLatitude();

                    origin = com.mapbox.geojson.Point.fromLngLat(lng, lat);
                    destination = com.mapbox.geojson.Point.fromLngLat(dstlng, dstlat);

                    enableLocationComponent(style);
                    initSource(style);
                    initLayers(style);

                    String styleUrl = mapboxMap.getStyle().getUri();
                    Log.i("Tawanda", "MapStyle: " + styleUrl);

                    getRoute(mapboxMap, origin, destination);

                    LatLngBounds latLngBounds = new LatLngBounds.Builder()
                            .include(new LatLng(lat, lng))
                            .include(new LatLng(dstlat, dstlng))
                            .build();


                }
            });

        }

    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .build();


            LocationComponent locationComponent = map.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build());
            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionManager = new PermissionsManager(this);
            permissionManager.requestLocationPermissions(this);
        }
    }


    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);

    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);

// Add the red marker icon image to the map
//        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
//                getResources().getDrawable(R.drawable.red_marker)));

// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -9f})));
    }

    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                Timber.d("Response code: " + response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }

                currentRoute = response.body().routes().get(0);

                Toast.makeText(MapActivity.this, String.format(
                        "dsadasdasd",
                        currentRoute.distance()), Toast.LENGTH_SHORT).show();

                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Timber.e("Error: %s", t.getMessage());
                Toast.makeText(MapActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
