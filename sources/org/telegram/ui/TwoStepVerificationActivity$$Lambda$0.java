package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class TwoStepVerificationActivity$$Lambda$0 implements OnEditorActionListener {
    private final TwoStepVerificationActivity arg$1;

    TwoStepVerificationActivity$$Lambda$0(TwoStepVerificationActivity twoStepVerificationActivity) {
        this.arg$1 = twoStepVerificationActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$0$TwoStepVerificationActivity(textView, i, keyEvent);
    }
}
