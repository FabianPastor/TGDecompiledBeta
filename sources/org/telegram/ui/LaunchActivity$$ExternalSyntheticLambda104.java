package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.ui.LocationActivity;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda104 implements LocationActivity.LocationActivityDelegate {
    public final /* synthetic */ HashMap f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda104(HashMap hashMap, int i) {
        this.f$0 = hashMap;
        this.f$1 = i;
    }

    public final void didSelectLocation(TLRPC$MessageMedia tLRPC$MessageMedia, int i, boolean z, int i2) {
        LaunchActivity.lambda$didReceivedNotification$86(this.f$0, this.f$1, tLRPC$MessageMedia, i, z, i2);
    }
}
