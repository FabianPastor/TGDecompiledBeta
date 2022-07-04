package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda23 implements Runnable {
    public final /* synthetic */ TLRPC.Document f$0;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda23(TLRPC.Document document) {
        this.f$0 = document;
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, this.f$0, 7);
    }
}
