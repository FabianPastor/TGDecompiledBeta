package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class GroupStickersActivity$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ GroupStickersActivity f$0;

    public /* synthetic */ GroupStickersActivity$$ExternalSyntheticLambda1(GroupStickersActivity groupStickersActivity) {
        this.f$0 = groupStickersActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$saveStickerSet$2(tLObject, tLRPC$TL_error);
    }
}
