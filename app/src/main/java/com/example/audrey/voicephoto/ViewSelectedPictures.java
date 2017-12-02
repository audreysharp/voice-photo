package com.example.audrey.voicephoto;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.example.audrey.voicephoto.MainActivity.databaseHelper;

/**
 * Search function
 */

public class ViewSelectedPictures extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.audrey.voicephoto.MESSAGE";
    ArrayList<String> imagePaths;
    private GridView gridview;
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_selected_pictures);

        //set text at top of screen to the search tag
        Intent intent = getIntent();
        String tag = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView tagText = (TextView) findViewById(R.id.tagName);
        tagText.setText("Images for: " + tag);

        // get list of images from database
        imagePaths = new ArrayList<>();
        imagePaths = databaseHelper.searchForImage(tag);
        Log.v("DEBUG", imagePaths.toString());

        if (imagePaths.size() > 0) {
            // populate grid view
            gridview = (GridView) findViewById(R.id.gridview);
            adapter = new ImageAdapter(this, imagePaths);
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
            Toast.makeText(this, "No images match the specified search input",
                    Toast.LENGTH_LONG).show();
        }

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
     * Go to 'view picture' mode
     */
    private void imageClick(String path) {
        Intent intent = new Intent(this, ViewSinglePictureActivity.class);
        String message = path;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
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

}
