package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class NewContactActivity$$Lambda$5 implements OnEditorActionListener {
    private final NewContactActivity arg$1;

    NewContactActivity$$Lambda$5(NewContactActivity newContactActivity) {
        this.arg$1 = newContactActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$7$NewContactActivity(textView, i, keyEvent);
    }
}
