package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class NotificationsSettingsActivity$$Lambda$9 implements RequestDelegate {
    private final NotificationsSettingsActivity arg$1;

    NotificationsSettingsActivity$$Lambda$9(NotificationsSettingsActivity notificationsSettingsActivity) {
        this.arg$1 = notificationsSettingsActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$null$3$NotificationsSettingsActivity(tLObject, tL_error);
    }
}
