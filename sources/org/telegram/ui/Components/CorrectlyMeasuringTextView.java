package org.telegram.ui.Components;

import android.content.Context;
import android.text.Layout;
import android.widget.TextView;

public class CorrectlyMeasuringTextView extends TextView {
    public CorrectlyMeasuringTextView(Context context) {
        super(context);
    }

    public void onMeasure(int wms, int hms) {
        super.onMeasure(wms, hms);
        try {
            Layout l = getLayout();
            if (l.getLineCount() > 1) {
                int maxw = 0;
                int i = l.getLineCount() - 1;
                while (true) {
                    int i2 = i;
                    if (i2 < 0) {
                        break;
                    }
                    maxw = Math.max(maxw, Math.round(l.getPaint().measureText(getText(), l.getLineStart(i2), l.getLineEnd(i2))));
                    i = i2 - 1;
                }
                super.onMeasure(Math.min((getPaddingLeft() + maxw) + getPaddingRight(), getMeasuredWidth()) | NUM, NUM | getMeasuredHeight());
            }
        } catch (Exception e) {
        }
    }
}
