package org.telegram.ui;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class GroupStickersActivity$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ GroupStickersActivity f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ GroupStickersActivity$$ExternalSyntheticLambda3(GroupStickersActivity groupStickersActivity, TLObject tLObject) {
        this.f$0 = groupStickersActivity;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$resolveStickerSet$2(this.f$1);
    }
}
