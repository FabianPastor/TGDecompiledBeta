package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ManageLinksActivity$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ ManageLinksActivity f$0;
    public final /* synthetic */ TLRPC.TL_chatInviteExported f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ ManageLinksActivity$$ExternalSyntheticLambda9(ManageLinksActivity manageLinksActivity, TLRPC.TL_chatInviteExported tL_chatInviteExported, TLRPC.TL_error tL_error, TLObject tLObject, boolean z) {
        this.f$0 = manageLinksActivity;
        this.f$1 = tL_chatInviteExported;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.m2603lambda$loadLinks$3$orgtelegramuiManageLinksActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
