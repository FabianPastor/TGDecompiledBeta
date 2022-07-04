package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LinkActionView$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ LinkActionView f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC$TL_chatInviteExported f$3;

    public /* synthetic */ LinkActionView$$ExternalSyntheticLambda10(LinkActionView linkActionView, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        this.f$0 = linkActionView;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = tLRPC$TL_chatInviteExported;
    }

    public final void run() {
        this.f$0.lambda$loadUsers$11(this.f$1, this.f$2, this.f$3);
    }
}
