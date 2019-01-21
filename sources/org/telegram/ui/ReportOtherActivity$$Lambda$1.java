package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class ReportOtherActivity$$Lambda$1 implements OnEditorActionListener {
    private final ReportOtherActivity arg$1;

    ReportOtherActivity$$Lambda$1(ReportOtherActivity reportOtherActivity) {
        this.arg$1 = reportOtherActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$1$ReportOtherActivity(textView, i, keyEvent);
    }
}
