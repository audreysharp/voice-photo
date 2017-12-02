package com.example.audrey.voicephoto;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static android.provider.MediaStore.EXTRA_OUTPUT;
import static com.example.audrey.voicephoto.MainActivity.databaseHelper;

/**
 * Takes picture
 */

public class CameraActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    EditText tag, lat, lon, location;
    ImageView imgview;
    File photoFile;
    Uri photoURI;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    String mCurrentPhotoPath;
    ArrayList<String> result;
    GoogleApiClient c = null;
    LocationRequest req = null;
    Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (c == null)
            c = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        takePicture();

        // add event listener for bottom navigation bar
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectFragment(item);
                        return false;
                    }
                });

    }

    /**
     * Save image, tag, location data to database
     *
     * @param v
     */
    public void save(View v) throws IOException {
        tag = (EditText) findViewById(R.id.tagEdit);
        lat = (EditText) findViewById(R.id.latEdit);
        lon = (EditText) findViewById(R.id.lonEdit);
        location = (EditText) findViewById(R.id.locEdit);

        String tagString = tag.getText().toString();
        String latString = lat.getText().toString();
        String lonString = lon.getText().toString();
        String locString = location.getText().toString();

        //make sure a tag is present
        if (tagString.equals("")) {
            Toast.makeText(CameraActivity.this, "Please enter a tag",
                    Toast.LENGTH_SHORT).show();
        } else {
            databaseHelper.addPhoto(mCurrentPhotoPath, tagString, latString, lonString, locString);
            Toast.makeText(CameraActivity.this, "Picture saved!",
                    Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Get user speech input
     *
     * @param v
     */
    public void speakTag(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak the tag you would like to give your image");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    /**
     * Method to take picture
     */
    protected void takePicture() {
        Intent takePictureIntent = new Intent(ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.v("Tag", "Error while creating file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(EXTRA_OUTPUT, photoFile.getAbsolutePath().toString());
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    /**
     * Activity result for taking picture and speaking tag
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        resultCode = RESULT_OK;
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Prompt user to speak tag
            speakTag(null);

            Bitmap mbmp = (Bitmap) data.getExtras().get("data");
            imgview = (ImageView) findViewById(R.id.imgv);
            imgview.setImageBitmap(mbmp);
            try {
                FileOutputStream out = new FileOutputStream(photoFile);
                mbmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {

                result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                tag = (EditText) findViewById(R.id.tagEdit);
                tag.setText(result.get(0));
            }
        } else {
            Log.v("Tag", "Something wrong in onActivityResult");
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Perform action when any item is selected.
     *
     * @param item Item that is selected.
     */
    protected void selectFragment(MenuItem item) {

        item.setChecked(true);

        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_item1: // view pictures
                Log.v("DEBUG", "View pictures");
                intent = new Intent (this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.action_item2: // take picture
                Log.v("DEBUG", "Take picture");
                intent = new Intent (this, CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.action_item3: // view places
                Log.v("DEBUG", "View places");
                intent = new Intent (this, ViewPlacesActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        c.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        c.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.v("Tag", "We are connected");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        loc = LocationServices.FusedLocationApi.getLastLocation(c);

        lat = (EditText) findViewById(R.id.latEdit);
        lon = (EditText) findViewById(R.id.lonEdit);
        location = (EditText) findViewById(R.id.locEdit);
        lat.setText(String.valueOf(loc.getLatitude()));
        lon.setText(String.valueOf(loc.getLongitude()));

        Log.v("Tag", "Lat = " + loc.getLatitude() + ", Long = " +
                loc.getLongitude());


        Geocoder gc = new Geocoder(this);
        try {
            List<android.location.Address> la =
                    gc.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

            for (android.location.Address x: la) {

                //if street name doesn't exist
                if (x.getThoroughfare() == null) {
                    location.setText(x.getLocality() + ", " + x.getSubAdminArea() + ", " +
                                    x.getAdminArea() + ", " + x.getPostalCode() + ", " + x.getCountryCode());
                } else {
                    location.setText(x.getThoroughfare() + ", " + x.getLocality() + ", " + x.getSubAdminArea() + ", " +
                                    x.getAdminArea() + ", " + x.getPostalCode() + ", " + x.getCountryCode());
                }

                if (x.getThoroughfare() != null)
                    Log.v("Tag", x.getThoroughfare());
                Log.v("Tag", x.getLocality());
                Log.v("Tag", x.getSubAdminArea());
                Log.v("Tag", x.getAdminArea());
                Log.v("Tag", x.getPostalCode());
                Log.v("Tag", x.getCountryName());
                //Log.v("Tag", "Address: " + x.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
