package com.commonfeaturelib.SelectImage.utility;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.commonfeaturelib.SelectImage.PhotoSelectPojo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by janarthananr on 16/5/18.
 */

public class ImageFetcher extends AsyncTask<Cursor, Void, ArrayList<PhotoSelectPojo>> {
    ArrayList<PhotoSelectPojo> LIST = new ArrayList<>();

    @Override
    protected ArrayList<PhotoSelectPojo> doInBackground(Cursor... cursors) {
        Cursor cursor = cursors[0];
        if (cursor != null) {
            int date = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
            int data = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int contenturl = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            try {
                while (cursor.moveToNext()) {
                    Uri curl = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + cursor.getInt(contenturl));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(cursor.getLong(date));
                    String mydate = Utility.getDateDifference(calendar);
                    PhotoSelectPojo photoSelectPojo = new PhotoSelectPojo();
                    photoSelectPojo.headerDate = "" + mydate;
                    photoSelectPojo.contentUrl = "" + curl;
                    photoSelectPojo.url = cursor.getString(data);
                    photoSelectPojo.scrollerDate = new SimpleDateFormat("MMMM yyyy").format(calendar.getTime());
                    LIST.add(photoSelectPojo);
                }
            } finally {
                cursor.close();
            }
        }

        return LIST;
    }

}
