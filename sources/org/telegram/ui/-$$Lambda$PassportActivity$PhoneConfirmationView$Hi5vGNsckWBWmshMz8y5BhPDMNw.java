package org.telegram.ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import org.telegram.ui.PassportActivity.PhoneConfirmationView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$PhoneConfirmationView$Hi5vGNsckWBWmshMz8y5BhPDMNw implements OnKeyListener {
    private final /* synthetic */ PhoneConfirmationView f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$PassportActivity$PhoneConfirmationView$Hi5vGNsckWBWmshMz8y5BhPDMNw(PhoneConfirmationView phoneConfirmationView, int i) {
        this.f$0 = phoneConfirmationView;
        this.f$1 = i;
    }

    public final boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$setParams$4$PassportActivity$PhoneConfirmationView(this.f$1, view, i, keyEvent);
    }
}
