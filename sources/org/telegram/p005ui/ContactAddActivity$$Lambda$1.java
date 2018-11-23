package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* renamed from: org.telegram.ui.ContactAddActivity$$Lambda$1 */
final /* synthetic */ class ContactAddActivity$$Lambda$1 implements OnEditorActionListener {
    private final ContactAddActivity arg$1;

    ContactAddActivity$$Lambda$1(ContactAddActivity contactAddActivity) {
        this.arg$1 = contactAddActivity;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$1$ContactAddActivity(textView, i, keyEvent);
    }
}
