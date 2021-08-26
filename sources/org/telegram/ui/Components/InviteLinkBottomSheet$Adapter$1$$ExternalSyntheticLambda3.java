package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.InviteLinkBottomSheet;

public final /* synthetic */ class InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ InviteLinkBottomSheet.Adapter.AnonymousClass1 f$0;

    public /* synthetic */ InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda3(InviteLinkBottomSheet.Adapter.AnonymousClass1 r1) {
        this.f$0 = r1;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$removeLink$3(tLObject, tLRPC$TL_error);
    }
}
