package com.example.audrey.voicephoto;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.audrey.voicephoto.MainActivity.databaseHelper;

public class ViewSinglePictureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_picture);

        Intent intent = getIntent();
        String path = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        ArrayList<String> imageDetails = databaseHelper.getImageFromPath(path);
        populateImageDetails(path, imageDetails);

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
     * Populate ImageView and TextViews
     * @param path
     * @param imageDetails
     */
    private void populateImageDetails(String path, ArrayList<String> imageDetails) {
        ImageView imageView = (ImageView) findViewById(R.id.singleImgView);
        EditText tagEdit = (EditText) findViewById(R.id.singleTagEdit);
        EditText latEdit = (EditText) findViewById(R.id.singleLatEdit);
        EditText lonEdit = (EditText) findViewById(R.id.singleLonEdit);

        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        tagEdit.setText(imageDetails.get(0));
        latEdit.setText(imageDetails.get(1));
        lonEdit.setText(imageDetails.get(2));
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
                // intent = new Intent (this, ViewPlacesActivity.class);
                // startActivity(intent);
                break;
        }
    }
}
