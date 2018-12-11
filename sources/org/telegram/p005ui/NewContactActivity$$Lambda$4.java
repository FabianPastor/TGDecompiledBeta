package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* renamed from: org.telegram.ui.NewContactActivity$$Lambda$4 */
final /* synthetic */ class NewContactActivity$$Lambda$4 implements OnEditorActionListener {
    private final NewContactActivity arg$1;

    NewContactActivity$$Lambda$4(NewContactActivity newContactActivity) {
        this.arg$1 = newContactActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$6$NewContactActivity(textView, i, keyEvent);
    }
}
