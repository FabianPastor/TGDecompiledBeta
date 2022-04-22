package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_messages_archivedStickers;

public final /* synthetic */ class ArchivedStickersActivity$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ArchivedStickersActivity f$0;
    public final /* synthetic */ TLRPC$TL_messages_archivedStickers f$1;

    public /* synthetic */ ArchivedStickersActivity$$ExternalSyntheticLambda1(ArchivedStickersActivity archivedStickersActivity, TLRPC$TL_messages_archivedStickers tLRPC$TL_messages_archivedStickers) {
        this.f$0 = archivedStickersActivity;
        this.f$1 = tLRPC$TL_messages_archivedStickers;
    }

    public final void run() {
        this.f$0.lambda$processResponse$3(this.f$1);
    }
}
