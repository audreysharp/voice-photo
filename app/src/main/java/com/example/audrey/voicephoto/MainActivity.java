package com.example.audrey.voicephoto;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Takes pictures adn adds a tag using a voice command
 * GPS coordinates saved to mark location of the image taken
 * Images saved and search using tag or location
 */

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db = null;
    File photoFile;

    private GridView gridview;
    private ImageAdapter gridSquare;
    ArrayList<String> imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // hide keyboard on launch

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

        // get list of images from database
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        imagePaths = new ArrayList<>();
        imagePaths = databaseHelper.getAllImages();

        if (imagePaths.size() > 0) {
            // populate grid view
            gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(MainActivity.this, imagePaths));
        } else {
            Toast.makeText(MainActivity.this, "You haven't taken any pictures yet!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Go to 'view picture' mode
     */
    public void imageClick(View v) {

    }

    /**
     * Helper methods
     */

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