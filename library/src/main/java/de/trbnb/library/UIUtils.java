package de.trbnb.library;

import android.content.Context;

/**
 * Created by thorben on 20.08.15.
 */
public class UIUtils {
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
