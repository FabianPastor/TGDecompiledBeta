package org.telegram.p005ui;

import java.util.HashMap;
import org.telegram.p005ui.LocationActivity.LocationActivityDelegate;
import org.telegram.tgnet.TLRPC.MessageMedia;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$37 */
final /* synthetic */ class LaunchActivity$$Lambda$37 implements LocationActivityDelegate {
    private final HashMap arg$1;
    private final int arg$2;

    LaunchActivity$$Lambda$37(HashMap hashMap, int i) {
        this.arg$1 = hashMap;
        this.arg$2 = i;
    }

    public void didSelectLocation(MessageMedia messageMedia, int i) {
        LaunchActivity.lambda$null$36$LaunchActivity(this.arg$1, this.arg$2, messageMedia, i);
    }
}
