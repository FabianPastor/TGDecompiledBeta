package org.telegram.ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import org.telegram.ui.CancelAccountDeletionActivity.LoginActivitySmsView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$ZA7-mAFfTFMlQCzBbBXS9uV3KIM implements OnKeyListener {
    private final /* synthetic */ LoginActivitySmsView f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$CancelAccountDeletionActivity$LoginActivitySmsView$ZA7-mAFfTFMlQCzBbBXS9uV3KIM(LoginActivitySmsView loginActivitySmsView, int i) {
        this.f$0 = loginActivitySmsView;
        this.f$1 = i;
    }

    public final boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$setParams$4$CancelAccountDeletionActivity$LoginActivitySmsView(this.f$1, view, i, keyEvent);
    }
}
