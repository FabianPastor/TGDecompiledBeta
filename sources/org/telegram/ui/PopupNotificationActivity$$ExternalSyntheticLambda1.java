package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.MessageObject;

public final /* synthetic */ class PopupNotificationActivity$$ExternalSyntheticLambda1 implements View.OnClickListener {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ PopupNotificationActivity$$ExternalSyntheticLambda1(int i, MessageObject messageObject) {
        this.f$0 = i;
        this.f$1 = messageObject;
    }

    public final void onClick(View view) {
        PopupNotificationActivity.lambda$getButtonsViewForMessage$5(this.f$0, this.f$1, view);
    }
}
