package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$FTx0IVP0vE85KAxrAB5y7NABR34 implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ TL_messages_stickerSet f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$FTx0IVP0vE85KAxrAB5y7NABR34(MediaDataController mediaDataController, TL_messages_stickerSet tL_messages_stickerSet) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_messages_stickerSet;
    }

    public final void run() {
        this.f$0.lambda$putSetToCache$11$MediaDataController(this.f$1);
    }
}
