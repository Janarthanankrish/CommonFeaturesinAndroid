package com.commonfeaturelib.SelectImage.interfaces;

import android.view.View;

import com.commonfeaturelib.SelectImage.PhotoSelectPojo;

/**
 * Created by janarthananr on 16/5/18.
 */

public interface OnSelectionListner {
    void OnClick(PhotoSelectPojo PhotoSelectPojo, View view, int position);

    void OnLongClick(PhotoSelectPojo photoSelectPojo, View view, int position);
}
