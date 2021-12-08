package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ManageLinksActivity$$ExternalSyntheticLambda16 implements Runnable {
    public final /* synthetic */ ManageLinksActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLRPC.TL_chatInviteExported f$2;

    public /* synthetic */ ManageLinksActivity$$ExternalSyntheticLambda16(ManageLinksActivity manageLinksActivity, TLRPC.TL_error tL_error, TLRPC.TL_chatInviteExported tL_chatInviteExported) {
        this.f$0 = manageLinksActivity;
        this.f$1 = tL_error;
        this.f$2 = tL_chatInviteExported;
    }

    public final void run() {
        this.f$0.m3275lambda$deleteLink$13$orgtelegramuiManageLinksActivity(this.f$1, this.f$2);
    }
}
