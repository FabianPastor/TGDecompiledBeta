package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PermanentLinkBottomSheet$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ PermanentLinkBottomSheet f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ PermanentLinkBottomSheet$$ExternalSyntheticLambda3(PermanentLinkBottomSheet permanentLinkBottomSheet, boolean z) {
        this.f$0 = permanentLinkBottomSheet;
        this.f$1 = z;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2448xdCLASSNAMEc4(this.f$1, tLObject, tL_error);
    }
}
