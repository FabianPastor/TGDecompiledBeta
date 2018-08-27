package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.ui.PassportActivity.PhoneConfirmationView;

final /* synthetic */ class PassportActivity$PhoneConfirmationView$$Lambda$0 implements OnEditorActionListener {
    private final PhoneConfirmationView arg$1;

    PassportActivity$PhoneConfirmationView$$Lambda$0(PhoneConfirmationView phoneConfirmationView) {
        this.arg$1 = phoneConfirmationView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$new$0$PassportActivity$PhoneConfirmationView(textView, i, keyEvent);
    }
}
