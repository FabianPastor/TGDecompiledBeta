package org.telegram.ui.Components;

import android.content.Context;
import android.widget.TextView;

public class CorrectlyMeasuringTextView extends TextView {
    public CorrectlyMeasuringTextView(Context context) {
        super(context);
    }

    public void onMeasure(int r6, int r7) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r5 = this;
        super.onMeasure(r6, r7);
        r6 = r5.getLayout();	 Catch:{ Exception -> 0x0053 }
        r7 = r6.getLineCount();	 Catch:{ Exception -> 0x0053 }
        r0 = 1;	 Catch:{ Exception -> 0x0053 }
        if (r7 > r0) goto L_0x000f;	 Catch:{ Exception -> 0x0053 }
    L_0x000e:
        return;	 Catch:{ Exception -> 0x0053 }
    L_0x000f:
        r7 = 0;	 Catch:{ Exception -> 0x0053 }
        r1 = r6.getLineCount();	 Catch:{ Exception -> 0x0053 }
        r1 = r1 - r0;	 Catch:{ Exception -> 0x0053 }
    L_0x0015:
        if (r1 < 0) goto L_0x0036;	 Catch:{ Exception -> 0x0053 }
    L_0x0017:
        r0 = r6.getPaint();	 Catch:{ Exception -> 0x0053 }
        r2 = r5.getText();	 Catch:{ Exception -> 0x0053 }
        r3 = r6.getLineStart(r1);	 Catch:{ Exception -> 0x0053 }
        r4 = r6.getLineEnd(r1);	 Catch:{ Exception -> 0x0053 }
        r0 = r0.measureText(r2, r3, r4);	 Catch:{ Exception -> 0x0053 }
        r0 = java.lang.Math.round(r0);	 Catch:{ Exception -> 0x0053 }
        r7 = java.lang.Math.max(r7, r0);	 Catch:{ Exception -> 0x0053 }
        r1 = r1 + -1;	 Catch:{ Exception -> 0x0053 }
        goto L_0x0015;	 Catch:{ Exception -> 0x0053 }
    L_0x0036:
        r6 = r5.getPaddingLeft();	 Catch:{ Exception -> 0x0053 }
        r7 = r7 + r6;	 Catch:{ Exception -> 0x0053 }
        r6 = r5.getPaddingRight();	 Catch:{ Exception -> 0x0053 }
        r7 = r7 + r6;	 Catch:{ Exception -> 0x0053 }
        r6 = r5.getMeasuredWidth();	 Catch:{ Exception -> 0x0053 }
        r6 = java.lang.Math.min(r7, r6);	 Catch:{ Exception -> 0x0053 }
        r7 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0053 }
        r6 = r6 | r7;	 Catch:{ Exception -> 0x0053 }
        r0 = r5.getMeasuredHeight();	 Catch:{ Exception -> 0x0053 }
        r7 = r7 | r0;	 Catch:{ Exception -> 0x0053 }
        super.onMeasure(r6, r7);	 Catch:{ Exception -> 0x0053 }
    L_0x0053:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.CorrectlyMeasuringTextView.onMeasure(int, int):void");
    }
}
