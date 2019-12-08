package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$LjXgSx2TmdbTqJUfGyG8-UY8Zck implements Runnable {
    private final /* synthetic */ IntCallback f$0;
    private final /* synthetic */ Updates f$1;

    public /* synthetic */ -$$Lambda$MessagesController$LjXgSx2TmdbTqJUfGyG8-UY8Zck(IntCallback intCallback, Updates updates) {
        this.f$0 = intCallback;
        this.f$1 = updates;
    }

    public final void run() {
        MessagesController.lambda$null$166(this.f$0, this.f$1);
    }
}
