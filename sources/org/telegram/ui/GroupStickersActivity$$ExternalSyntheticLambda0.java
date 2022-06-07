package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class GroupStickersActivity$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ GroupStickersActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ GroupStickersActivity$$ExternalSyntheticLambda0(GroupStickersActivity groupStickersActivity, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = groupStickersActivity;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$saveStickerSet$1(this.f$1);
    }
}
