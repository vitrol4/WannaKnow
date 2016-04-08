package br.com.vitrol4.wannaknow;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by victor on 21/08/15.
 */
public abstract class ManagerLocationActivity extends ListActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int PERMISSION_REQUEST_CODE = 1;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static boolean turnGPS = true;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;

    public static boolean checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(WKApplication.context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
            Log.i("GPS", "ja tem permissao");
        } else {
            Log.i("GPS", "pedindo permissao");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPlayServices()) {
            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(ManagerLocationActivity.this, "Precisamos dessa permissão para acessar seu GPS e buscar o locais mais próximos à você.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void displayPromptForEnablingGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Deseja ativar o GPS?";

        builder.setMessage(message)
                .setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Não",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                turnGPS = false;
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    private Location getLocation() {
        if (isGPSEnabled()) {
            if (mGoogleApiClient.isConnected()) {
                if (!checkPermission()) {
                    requestPermission(this);
                } else {
                    Log.i("GPS", "request location");
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                }
            }
            return mLastLocation;

        } else if (turnGPS) {
            displayPromptForEnablingGPS();
        }
        return null;
    }

    protected abstract void setLocation(Location location);

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    protected float calcDistance(Location start, Location end) {
        return start.distanceTo(end);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        int UPDATE_INTERVAL = 5000; // 5 sec
        int FATEST_INTERVAL = 1000; // 1 sec
        int DISPLACEMENT = 10; // 10 meters

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "Esse dispositivo não é suportado", Toast.LENGTH_LONG).show();
            }

            return false;
        }

        return true;
    }

    /* Deprecated */
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Toast.makeText(this, "Esse dispositivo não é suportado", Toast.LENGTH_LONG).show();
//            }
//            return false;
//        }
//        return true;
//    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                if (!checkPermission()) {
                    requestPermission(this);
                } else {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }
            }

        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e("GPS", "A conexão falhou erro numero " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        setLocation(getLocation());
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        setLocation(getLocation());
    }

}
