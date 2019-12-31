package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$0qiVPxfoMMz32zSLwelNxxrNu3U implements LocationActivityDelegate {
    private final /* synthetic */ HashMap f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$0qiVPxfoMMz32zSLwelNxxrNu3U(HashMap hashMap, int i) {
        this.f$0 = hashMap;
        this.f$1 = i;
    }

    public final void didSelectLocation(MessageMedia messageMedia, int i, boolean z, int i2) {
        LaunchActivity.lambda$null$51(this.f$0, this.f$1, messageMedia, i, z, i2);
    }
}
