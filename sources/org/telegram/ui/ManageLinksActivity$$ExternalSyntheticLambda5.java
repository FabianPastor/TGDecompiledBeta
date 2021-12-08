package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ManageLinksActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ ManageLinksActivity f$0;
    public final /* synthetic */ TLRPC.TL_chatInviteExported f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ ManageLinksActivity$$ExternalSyntheticLambda5(ManageLinksActivity manageLinksActivity, TLRPC.TL_chatInviteExported tL_chatInviteExported, boolean z) {
        this.f$0 = manageLinksActivity;
        this.f$1 = tL_chatInviteExported;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3283lambda$loadLinks$5$orgtelegramuiManageLinksActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
