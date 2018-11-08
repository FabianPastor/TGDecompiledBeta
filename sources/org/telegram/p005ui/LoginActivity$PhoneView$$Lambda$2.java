package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.p005ui.LoginActivity.PhoneView;

/* renamed from: org.telegram.ui.LoginActivity$PhoneView$$Lambda$2 */
final /* synthetic */ class LoginActivity$PhoneView$$Lambda$2 implements OnEditorActionListener {
    private final PhoneView arg$1;

    LoginActivity$PhoneView$$Lambda$2(PhoneView phoneView) {
        this.arg$1 = phoneView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$new$4$LoginActivity$PhoneView(textView, i, keyEvent);
    }
}
