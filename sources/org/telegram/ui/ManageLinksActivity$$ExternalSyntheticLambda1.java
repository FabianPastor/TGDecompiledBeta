package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ManageLinksActivity$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ ManageLinksActivity f$0;

    public /* synthetic */ ManageLinksActivity$$ExternalSyntheticLambda1(ManageLinksActivity manageLinksActivity) {
        this.f$0 = manageLinksActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3280lambda$loadLinks$2$orgtelegramuiManageLinksActivity(tLObject, tL_error);
    }
}
