package com.example.audrey.voicephoto;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by audrey on 11/19/17.
 */

public class ImageAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> imagePaths;
    private static LayoutInflater inflater = null;

    public ImageAdapter(Context c, ArrayList<String> p) {
        // TODO Auto-generated constructor stub
        context = c;
        imagePaths = p;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_single, null);
            imageView = (ImageView) convertView.findViewById(R.id.imgView);

            String imagePath = imagePaths.get(position);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            imageView.getLayoutParams().height = 400;
            imageView.getLayoutParams().width = 400;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }

        return imageView;
    }

}