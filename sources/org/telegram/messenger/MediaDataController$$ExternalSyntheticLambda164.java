package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda164 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.TL_messages_stickerSet f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda164(MediaDataController mediaDataController, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        this.f$0 = mediaDataController;
        this.f$1 = tL_messages_stickerSet;
    }

    public final void run() {
        this.f$0.m817x51155003(this.f$1);
    }
}
