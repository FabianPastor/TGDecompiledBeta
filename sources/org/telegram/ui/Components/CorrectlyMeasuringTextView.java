package org.telegram.ui.Components;

import android.content.Context;
import android.text.Layout;
import android.widget.TextView;
/* loaded from: classes3.dex */
public class CorrectlyMeasuringTextView extends TextView {
    public CorrectlyMeasuringTextView(Context context) {
        super(context);
    }

    @Override // android.widget.TextView, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        try {
            Layout layout = getLayout();
            if (layout.getLineCount() <= 1) {
                return;
            }
            int i3 = 0;
            for (int lineCount = layout.getLineCount() - 1; lineCount >= 0; lineCount--) {
                i3 = Math.max(i3, Math.round(layout.getPaint().measureText(getText(), layout.getLineStart(lineCount), layout.getLineEnd(lineCount))));
            }
            super.onMeasure(Math.min(i3 + getPaddingLeft() + getPaddingRight(), getMeasuredWidth()) | NUM, NUM | getMeasuredHeight());
        } catch (Exception unused) {
        }
    }
}
