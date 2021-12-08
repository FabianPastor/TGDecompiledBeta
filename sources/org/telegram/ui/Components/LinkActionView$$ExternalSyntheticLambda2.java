package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LinkActionView$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ LinkActionView f$0;
    public final /* synthetic */ TLRPC.TL_chatInviteExported f$1;

    public /* synthetic */ LinkActionView$$ExternalSyntheticLambda2(LinkActionView linkActionView, TLRPC.TL_chatInviteExported tL_chatInviteExported) {
        this.f$0 = linkActionView;
        this.f$1 = tL_chatInviteExported;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2398lambda$loadUsers$12$orgtelegramuiComponentsLinkActionView(this.f$1, tLObject, tL_error);
    }
}
