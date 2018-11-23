package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class ExtendedBitmapDrawable extends BitmapDrawable {
    private boolean canInvert;

    public ExtendedBitmapDrawable(Bitmap bitmap, boolean invert) {
        super(bitmap);
        this.canInvert = invert;
    }

    public boolean isCanInvert() {
        return this.canInvert;
    }
}
