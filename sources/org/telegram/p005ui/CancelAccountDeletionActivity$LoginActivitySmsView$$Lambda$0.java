package org.telegram.p005ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.p005ui.CancelAccountDeletionActivity.LoginActivitySmsView;

/* renamed from: org.telegram.ui.CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$0 */
final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$0 implements OnEditorActionListener {
    private final LoginActivitySmsView arg$1;

    CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$0(LoginActivitySmsView loginActivitySmsView) {
        this.arg$1 = loginActivitySmsView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$new$0$CancelAccountDeletionActivity$LoginActivitySmsView(textView, i, keyEvent);
    }
}
