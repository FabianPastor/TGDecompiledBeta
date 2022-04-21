package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LinkEditActivity$$ExternalSyntheticLambda8 implements Runnable {
    public final /* synthetic */ LinkEditActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ LinkEditActivity$$ExternalSyntheticLambda8(LinkEditActivity linkEditActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = linkEditActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m2401lambda$onCreateClicked$7$orgtelegramuiLinkEditActivity(this.f$1, this.f$2);
    }
}
