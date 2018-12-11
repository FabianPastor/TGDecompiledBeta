package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* renamed from: org.telegram.ui.ChangeBioActivity$$Lambda$1 */
final /* synthetic */ class ChangeBioActivity$$Lambda$1 implements OnEditorActionListener {
    private final ChangeBioActivity arg$1;

    ChangeBioActivity$$Lambda$1(ChangeBioActivity changeBioActivity) {
        this.arg$1 = changeBioActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$1$ChangeBioActivity(textView, i, keyEvent);
    }
}
