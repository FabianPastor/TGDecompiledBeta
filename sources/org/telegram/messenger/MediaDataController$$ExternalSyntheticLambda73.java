package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda73 implements RequestDelegate {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC.StickerSet f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda73(MediaDataController mediaDataController, TLRPC.StickerSet stickerSet, int i) {
        this.f$0 = mediaDataController;
        this.f$1 = stickerSet;
        this.f$2 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2115x5ec8cd67(this.f$1, this.f$2, tLObject, tL_error);
    }
}
