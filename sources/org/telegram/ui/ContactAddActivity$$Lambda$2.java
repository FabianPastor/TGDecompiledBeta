package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

final /* synthetic */ class ContactAddActivity$$Lambda$2 implements OnEditorActionListener {
    private final ContactAddActivity arg$1;

    ContactAddActivity$$Lambda$2(ContactAddActivity contactAddActivity) {
        this.arg$1 = contactAddActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$2$ContactAddActivity(textView, i, keyEvent);
    }
}
