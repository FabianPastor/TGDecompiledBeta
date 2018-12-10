package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* renamed from: org.telegram.ui.ChangeUsernameActivity$$Lambda$1 */
final /* synthetic */ class ChangeUsernameActivity$$Lambda$1 implements OnEditorActionListener {
    private final ChangeUsernameActivity arg$1;

    ChangeUsernameActivity$$Lambda$1(ChangeUsernameActivity changeUsernameActivity) {
        this.arg$1 = changeUsernameActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$1$ChangeUsernameActivity(textView, i, keyEvent);
    }
}
