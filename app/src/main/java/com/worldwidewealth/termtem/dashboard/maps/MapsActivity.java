package com.worldwidewealth.termtem.dashboard.maps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.worldwidewealth.termtem.MyAppcompatActivity;
import com.worldwidewealth.termtem.R;

public class MapsActivity extends MyAppcompatActivity {

    public static final String TAG = MapsActivity.class.getSimpleName();
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.e(TAG, "Google maps key: "+getString(R.string.google_maps_key));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
*/
}
