package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ManageLinksActivity$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ ManageLinksActivity f$0;
    public final /* synthetic */ TLRPC.TL_chatInviteExported f$1;

    public /* synthetic */ ManageLinksActivity$$ExternalSyntheticLambda3(ManageLinksActivity manageLinksActivity, TLRPC.TL_chatInviteExported tL_chatInviteExported) {
        this.f$0 = manageLinksActivity;
        this.f$1 = tL_chatInviteExported;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3934lambda$revokeLink$16$orgtelegramuiManageLinksActivity(this.f$1, tLObject, tL_error);
    }
}
