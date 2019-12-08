package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$k_Aic-iK2zJ3Y91NflwmRI9TpjI implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ TL_messages_stickerSet f$1;

    public /* synthetic */ -$$Lambda$MediaDataController$k_Aic-iK2zJ3Y91NflwmRI9TpjI(MediaDataController mediaDataController, TL_messages_stickerSet tL_messages_stickerSet) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_messages_stickerSet;
    }

    public final void run() {
        this.f$0.lambda$replaceStickerSet$6$MediaDataController(this.f$1);
    }
}
