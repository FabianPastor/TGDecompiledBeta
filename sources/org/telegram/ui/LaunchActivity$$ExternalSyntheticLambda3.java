package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LocationActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda3 implements LocationActivity.LocationActivityDelegate {
    public final /* synthetic */ HashMap f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda3(HashMap hashMap, int i) {
        this.f$0 = hashMap;
        this.f$1 = i;
    }

    public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        LaunchActivity.lambda$didReceivedNotification$83(this.f$0, this.f$1, messageMedia, i, z, i2);
    }
}
