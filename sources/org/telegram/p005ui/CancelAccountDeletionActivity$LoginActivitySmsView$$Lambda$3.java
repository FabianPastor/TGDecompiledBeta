package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.p005ui.CancelAccountDeletionActivity.LoginActivitySmsView;

/* renamed from: org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$3 */
final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$3 implements OnEditorActionListener {
    private final LoginActivitySmsView arg$1;

    CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$3(LoginActivitySmsView loginActivitySmsView) {
        this.arg$1 = loginActivitySmsView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.mo15926x90f7b9c2(textView, i, keyEvent);
    }
}
