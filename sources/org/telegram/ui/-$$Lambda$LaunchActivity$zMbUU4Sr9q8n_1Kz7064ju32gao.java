package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$zMbUU4Sr9q8n_1Kz7064ju32gao implements LocationActivityDelegate {
    private final /* synthetic */ HashMap f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$zMbUU4Sr9q8n_1Kz7064ju32gao(HashMap hashMap, int i) {
        this.f$0 = hashMap;
        this.f$1 = i;
    }

    public final void didSelectLocation(MessageMedia messageMedia, int i, boolean z, int i2) {
        LaunchActivity.lambda$null$46(this.f$0, this.f$1, messageMedia, i, z, i2);
    }
}
