package com.commonfeaturelib.SelectImage.interfaces;

import com.commonfeaturelib.SelectImage.activity.SelectMultipleImages;

/**
 * Created by janarthananr on 16/5/18.
 */

public interface FastScrollStateChangeListener {
    void onFastScrollStart(SelectMultipleImages fastScroller);

    void onFastScrollStop(SelectMultipleImages fastScroller);
}
