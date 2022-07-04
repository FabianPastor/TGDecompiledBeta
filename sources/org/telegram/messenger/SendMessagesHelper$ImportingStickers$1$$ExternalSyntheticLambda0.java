package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$ImportingStickers$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SendMessagesHelper.ImportingStickers.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_stickers_createStickerSet f$2;
    public final /* synthetic */ TLObject f$3;

    public /* synthetic */ SendMessagesHelper$ImportingStickers$1$$ExternalSyntheticLambda0(SendMessagesHelper.ImportingStickers.AnonymousClass1 r1, TLRPC.TL_error tL_error, TLRPC.TL_stickers_createStickerSet tL_stickers_createStickerSet, TLObject tLObject) {
        this.f$0 = r1;
        this.f$1 = tL_error;
        this.f$2 = tL_stickers_createStickerSet;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.m2390xCLASSNAMEdbdb1(this.f$1, this.f$2, this.f$3);
    }
}
