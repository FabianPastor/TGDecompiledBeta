package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.InviteLinkBottomSheet;

public final /* synthetic */ class InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ InviteLinkBottomSheet.Adapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ InviteLinkBottomSheet$Adapter$1$$ExternalSyntheticLambda1(InviteLinkBottomSheet.Adapter.AnonymousClass1 r1, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = r1;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m4087x37c2CLASSNAME(this.f$1, this.f$2);
    }
}
