package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class NotificationsSettingsActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ NotificationsSettingsActivity f$0;

    public /* synthetic */ NotificationsSettingsActivity$$ExternalSyntheticLambda7(NotificationsSettingsActivity notificationsSettingsActivity) {
        this.f$0 = notificationsSettingsActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$3(tLObject, tLRPC$TL_error);
    }
}
