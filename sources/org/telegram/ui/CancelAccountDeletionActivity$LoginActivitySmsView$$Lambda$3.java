package org.telegram.ui;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView;

final /* synthetic */ class CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$3 implements OnEditorActionListener {
    private final LoginActivitySmsView arg$1;

    CancelAccountDeletionActivity$LoginActivitySmsView$$Lambda$3(LoginActivitySmsView loginActivitySmsView) {
        this.arg$1 = loginActivitySmsView;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$setParams$5$CancelAccountDeletionActivity$LoginActivitySmsView(textView, i, keyEvent);
    }
}
