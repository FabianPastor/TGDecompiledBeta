package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class SharedMediaLayout$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ SharedMediaLayout f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLObject f$4;

    public /* synthetic */ SharedMediaLayout$$ExternalSyntheticLambda10(SharedMediaLayout sharedMediaLayout, TLRPC$TL_error tLRPC$TL_error, int i, int i2, TLObject tLObject) {
        this.f$0 = sharedMediaLayout;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadFastScrollData$12(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
