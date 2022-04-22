package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LinkActionView$$ExternalSyntheticLambda11 implements RequestDelegate {
    public final /* synthetic */ LinkActionView f$0;
    public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;

    public /* synthetic */ LinkActionView$$ExternalSyntheticLambda11(LinkActionView linkActionView, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        this.f$0 = linkActionView;
        this.f$1 = tLRPC$TL_chatInviteExported;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadUsers$12(this.f$1, tLObject, tLRPC$TL_error);
    }
}
