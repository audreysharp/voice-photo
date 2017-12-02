package com.example.audrey.voicephoto;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.speech.RecognizerIntent;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import static android.R.attr.tag;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Takes pictures and adds a tag using a voice command
 * GPS coordinates saved to mark location of the image taken
 * Images saved and search using tag or location
 */

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.audrey.voicephoto.MESSAGE";

    protected static DatabaseHelper databaseHelper;
    ImageAdapter adapter;

    File photoFile;
    private GridView gridview;
    private ImageAdapter gridSquare;
    ArrayList<String> imagePaths;
    EditText searchTag;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    ArrayList<String> result;

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
        databaseHelper = new DatabaseHelper(MainActivity.this);
        imagePaths = new ArrayList<>();
        imagePaths = databaseHelper.getAllImages();
        Log.v("DEBUG", imagePaths.toString());

        if (imagePaths.size() > 0) {
            // populate grid view
            gridview = (GridView) findViewById(R.id.gridview);
            adapter = new ImageAdapter(MainActivity.this, imagePaths);
            gridview.setAdapter(adapter);

            // set GridView item click listener
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ImageAdapter.ViewHolder viewHolder = (ImageAdapter.ViewHolder) view.getTag();
                    String tag = viewHolder.imageView.getTag().toString();
                    Log.v("DEBUG", "GridView " + tag);
                    imageClick(tag);
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "You haven't taken any pictures yet!",
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
                "Speak what you would like to search for");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

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
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {

                result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                searchTag = (EditText) findViewById(R.id.search_bar_edit_text);
                searchTag.setText(result.get(0));
                findViewById(R.id.findButton).callOnClick();
            }
        } else {
            Log.v("Tag", "Something wrong in onActivityResult");
        }
    }

    //find images with given search input
    public void find(View v) {
        searchTag = (EditText) findViewById(R.id.search_bar_edit_text);

        //makes sure search bar has text to search
        if (searchTag.getText().toString().matches("")) {
            Toast.makeText(this, "Type in search input", Toast.LENGTH_SHORT).show();
        } else {
            String searchTagStr = searchTag.getText().toString();
            searchTag.setText("");

            Log.v("Tag", searchTagStr);
            Intent intent = new Intent(this, ViewSelectedPictures.class);
            intent.putExtra(EXTRA_MESSAGE, searchTagStr);
            startActivity(intent);
        }

    }

    /**
     * Go to 'view picture' mode
     */
    private void imageClick(String path) {
        Intent intent = new Intent(this, ViewSinglePictureActivity.class);
        String message = path;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
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
                intent = new Intent (this, ViewPlacesActivity.class);
                startActivity(intent);
                break;
        }
    }

}
