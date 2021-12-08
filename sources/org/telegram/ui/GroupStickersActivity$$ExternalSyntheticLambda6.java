package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class GroupStickersActivity$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ GroupStickersActivity f$0;

    public /* synthetic */ GroupStickersActivity$$ExternalSyntheticLambda6(GroupStickersActivity groupStickersActivity) {
        this.f$0 = groupStickersActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$resolveStickerSet$3(tLObject, tLRPC$TL_error);
    }
}
