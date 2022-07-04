package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupStickersActivity$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ GroupStickersActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ GroupStickersActivity$$ExternalSyntheticLambda0(GroupStickersActivity groupStickersActivity, TLRPC.TL_error tL_error) {
        this.f$0 = groupStickersActivity;
        this.f$1 = tL_error;
    }

    public final void run() {
        this.f$0.m3595lambda$saveStickerSet$1$orgtelegramuiGroupStickersActivity(this.f$1);
    }
}
