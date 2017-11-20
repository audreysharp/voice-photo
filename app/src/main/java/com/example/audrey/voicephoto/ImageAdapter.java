package com.example.audrey.voicephoto;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
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
        View img = convertView;
        ViewHolder holder;
        if (img == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            img = inflater.inflate(R.layout.grid_single, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) img.findViewById(R.id.imgView);
            String imagePath = imagePaths.get(position);
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            img.setTag(holder);
        } else {
            holder = (ViewHolder) img.getTag();
        }

        return img;
    }

    static class ViewHolder {
        ImageView imageView;
    }

}
