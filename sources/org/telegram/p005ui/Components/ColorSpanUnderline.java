package org.telegram.p005ui.Components;

import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

/* renamed from: org.telegram.ui.Components.ColorSpanUnderline */
public class ColorSpanUnderline extends ForegroundColorSpan {
    public ColorSpanUnderline(int color) {
        super(color);
    }

    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(true);
    }
}
