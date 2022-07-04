package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Document;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda120 implements Runnable {
    public final /* synthetic */ TLRPC$Document f$0;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda120(TLRPC$Document tLRPC$Document) {
        this.f$0 = tLRPC$Document;
    }

    public final void run() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 0, this.f$0, 7);
    }
}
