package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.InviteLinkBottomSheet;

public final /* synthetic */ class InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ InviteLinkBottomSheet.Adapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda0(InviteLinkBottomSheet.Adapter.AnonymousClass1 r1, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = r1;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$removeLink$2(this.f$1);
    }
}
