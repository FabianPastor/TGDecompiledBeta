package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ManageLinksActivity$$ExternalSyntheticLambda15 implements Runnable {
    public final /* synthetic */ ManageLinksActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ TLRPC.TL_chatInviteExported f$3;

    public /* synthetic */ ManageLinksActivity$$ExternalSyntheticLambda15(ManageLinksActivity manageLinksActivity, TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_chatInviteExported tL_chatInviteExported) {
        this.f$0 = manageLinksActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = tL_chatInviteExported;
    }

    public final void run() {
        this.f$0.m3935lambda$revokePermanent$11$orgtelegramuiManageLinksActivity(this.f$1, this.f$2, this.f$3);
    }
}
