package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.messenger.MessageObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PopupNotificationActivity$ox3mIPlvmBDmNDp_7DLxqyRSnLI implements OnClickListener {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ MessageObject f$1;

    public /* synthetic */ -$$Lambda$PopupNotificationActivity$ox3mIPlvmBDmNDp_7DLxqyRSnLI(int i, MessageObject messageObject) {
        this.f$0 = i;
        this.f$1 = messageObject;
    }

    public final void onClick(View view) {
        PopupNotificationActivity.lambda$getButtonsViewForMessage$5(this.f$0, this.f$1, view);
    }
}
