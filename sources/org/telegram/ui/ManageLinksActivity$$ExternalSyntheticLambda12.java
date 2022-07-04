package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ManageLinksActivity$$ExternalSyntheticLambda12 implements Runnable {
    public final /* synthetic */ ManageLinksActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ ManageLinksActivity$$ExternalSyntheticLambda12(ManageLinksActivity manageLinksActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = manageLinksActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m3927lambda$loadLinks$0$orgtelegramuiManageLinksActivity(this.f$1, this.f$2);
    }
}
