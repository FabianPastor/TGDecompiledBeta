package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class NotificationsSettingsActivity$$Lambda$4 implements RequestDelegate {
    static final RequestDelegate $instance = new NotificationsSettingsActivity$$Lambda$4();

    private NotificationsSettingsActivity$$Lambda$4() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        NotificationsSettingsActivity.lambda$null$5$NotificationsSettingsActivity(tLObject, tL_error);
    }
}
