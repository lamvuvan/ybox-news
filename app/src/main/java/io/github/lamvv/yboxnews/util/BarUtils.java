package io.github.lamvv.yboxnews.util;

import android.content.Context;
import android.content.res.TypedArray;

import io.github.lamvv.yboxnews.R;

/**
 * Created by lamvu on 10/20/2016.
 */

public class BarUtils {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

}
