package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC.TL_messages_stickerSet f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda4(MediaDataController mediaDataController, boolean z, TLRPC.TL_messages_stickerSet tL_messages_stickerSet, int i, String str, boolean z2) {
        this.f$0 = mediaDataController;
        this.f$1 = z;
        this.f$2 = tL_messages_stickerSet;
        this.f$3 = i;
        this.f$4 = str;
        this.f$5 = z2;
    }

    public final void run() {
        this.f$0.m855x6a43067f(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
