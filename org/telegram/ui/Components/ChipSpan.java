package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import org.telegram.messenger.AndroidUtilities;

public class ChipSpan extends ImageSpan {
    public int uid;

    public ChipSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
        if (fm == null) {
            fm = new FontMetricsInt();
        }
        int sz = super.getSize(paint, text, start, end, fm);
        int offset = AndroidUtilities.dp(6.0f);
        int w = (fm.bottom - fm.top) / 2;
        fm.top = (-w) - offset;
        fm.bottom = w - offset;
        fm.ascent = (-w) - offset;
        fm.leading = 0;
        fm.descent = w - offset;
        return sz;
    }
}
