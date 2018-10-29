package org.telegram.ui;

import java.util.HashMap;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.ui.LocationActivity.LocationActivityDelegate;

final /* synthetic */ class LaunchActivity$$Lambda$36 implements LocationActivityDelegate {
    private final HashMap arg$1;
    private final int arg$2;

    LaunchActivity$$Lambda$36(HashMap hashMap, int i) {
        this.arg$1 = hashMap;
        this.arg$2 = i;
    }

    public void didSelectLocation(MessageMedia messageMedia, int i) {
        LaunchActivity.lambda$null$34$LaunchActivity(this.arg$1, this.arg$2, messageMedia, i);
    }
}
