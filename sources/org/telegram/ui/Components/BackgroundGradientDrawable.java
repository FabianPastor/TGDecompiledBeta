package org.telegram.ui.Components;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;

public class BackgroundGradientDrawable extends GradientDrawable {
    private int[] colors;

    public BackgroundGradientDrawable(Orientation orientation, int[] iArr) {
        super(orientation, iArr);
        this.colors = iArr;
    }

    public int[] getColorsList() {
        return this.colors;
    }
}
