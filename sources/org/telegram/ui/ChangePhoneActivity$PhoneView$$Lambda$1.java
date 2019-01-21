package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.ui.ChangePhoneActivity.PhoneView;

final /* synthetic */ class ChangePhoneActivity$PhoneView$$Lambda$1 implements OnEditorActionListener {
    private final PhoneView arg$1;

    ChangePhoneActivity$PhoneView$$Lambda$1(PhoneView phoneView) {
        this.arg$1 = phoneView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$new$3$ChangePhoneActivity$PhoneView(textView, i, keyEvent);
    }
}
