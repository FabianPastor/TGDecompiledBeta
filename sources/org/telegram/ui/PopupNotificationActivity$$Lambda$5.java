package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.messenger.MessageObject;

final /* synthetic */ class PopupNotificationActivity$$Lambda$5 implements OnClickListener {
    private final int arg$1;
    private final MessageObject arg$2;

    PopupNotificationActivity$$Lambda$5(int i, MessageObject messageObject) {
        this.arg$1 = i;
        this.arg$2 = messageObject;
    }

    public void onClick(View view) {
        PopupNotificationActivity.lambda$getButtonsViewForMessage$5$PopupNotificationActivity(this.arg$1, this.arg$2, view);
    }
}
