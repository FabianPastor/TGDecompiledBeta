package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PermanentLinkBottomSheet$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ PermanentLinkBottomSheet f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ PermanentLinkBottomSheet$$ExternalSyntheticLambda2(PermanentLinkBottomSheet permanentLinkBottomSheet, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        this.f$0 = permanentLinkBottomSheet;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$generateLink$2(this.f$1, this.f$2, this.f$3);
    }
}
