package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* renamed from: org.telegram.ui.TwoStepVerificationActivity$$Lambda$3 */
final /* synthetic */ class TwoStepVerificationActivity$$Lambda$3 implements OnEditorActionListener {
    private final TwoStepVerificationActivity arg$1;

    TwoStepVerificationActivity$$Lambda$3(TwoStepVerificationActivity twoStepVerificationActivity) {
        this.arg$1 = twoStepVerificationActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$10$TwoStepVerificationActivity(textView, i, keyEvent);
    }
}
