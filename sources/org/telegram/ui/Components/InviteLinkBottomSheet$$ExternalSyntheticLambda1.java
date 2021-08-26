package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class InviteLinkBottomSheet$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ InviteLinkBottomSheet f$0;

    public /* synthetic */ InviteLinkBottomSheet$$ExternalSyntheticLambda1(InviteLinkBottomSheet inviteLinkBottomSheet) {
        this.f$0 = inviteLinkBottomSheet;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadUsers$2(tLObject, tLRPC$TL_error);
    }
}
