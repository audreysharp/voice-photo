package com.example.audrey.voicephoto;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static com.example.audrey.voicephoto.MainActivity.databaseHelper;

public class ViewPlacesActivity extends AppCompatActivity implements OnMapReadyCallback {
    //GoogleMap mmap;
    String tagAndLoc, path;
    double lat, lon;
    ArrayList<List<String>> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_view_places);

        //get list of image attributes (index order: path, latitude, longitude, location, path) for all images
        imageList =  databaseHelper.imagesForMap();

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //mmap = googleMap;
        LatLng marker = null;

        //inserts markers for every image
        for (List<String> images : imageList) {
            lat = Double.parseDouble(images.get(1));
            lon = Double.parseDouble(images.get(2));
            path = images.get(4);
            tagAndLoc = "Tag: " + images.get(0) + "\nLatitude: " + lat + "\nLongitude: " + lon + "\nLocation: " + images.get(3);

            marker = new LatLng(lat, lon);
            googleMap.addMarker(new MarkerOptions().position(marker).title("Image Information " + path).snippet(tagAndLoc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            //method to add multiple lines to marker
            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    Context context = getApplicationContext();

                    //str2[0]=title and str2[2]=path
                    String str = marker.getTitle();
                    String[] str2 = str.split(" ");

                    LinearLayout info = new LinearLayout(context);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(context);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(str2[0] + " " + str2[1]);

                    TextView snippet = new TextView(context);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    ImageView image = new ImageView(context);
                    image.setImageBitmap(BitmapFactory.decodeFile(str2[2]));

                    info.addView(title);
                    info.addView(snippet);
                    info.addView(image);

                    return info;
                }
            });
        }

        //zoom in on most recent photo
        if (marker != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 17));
        }
    }

}
