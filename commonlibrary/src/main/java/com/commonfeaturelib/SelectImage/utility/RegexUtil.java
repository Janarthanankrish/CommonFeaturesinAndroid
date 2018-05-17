package com.commonfeaturelib.SelectImage.utility;

/**
 * Created by janarthananr on 16/5/18.
 */

public class RegexUtil {
    private static final String GIF_PATTERN =
            "(.+?)\\.gif$";

    public boolean checkGif(String path) {
        return path.matches(GIF_PATTERN);
    }
}
