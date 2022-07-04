package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class GroupStickersActivity$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ GroupStickersActivity f$0;

    public /* synthetic */ GroupStickersActivity$$ExternalSyntheticLambda1(GroupStickersActivity groupStickersActivity) {
        this.f$0 = groupStickersActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3596lambda$saveStickerSet$2$orgtelegramuiGroupStickersActivity(tLObject, tL_error);
    }
}
