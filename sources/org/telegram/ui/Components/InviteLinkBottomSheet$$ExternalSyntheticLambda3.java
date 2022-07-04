package org.telegram.ui.Components;

import java.util.List;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class InviteLinkBottomSheet$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ InviteLinkBottomSheet f$0;
    public final /* synthetic */ List f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ InviteLinkBottomSheet$$ExternalSyntheticLambda3(InviteLinkBottomSheet inviteLinkBottomSheet, List list, boolean z, boolean z2) {
        this.f$0 = inviteLinkBottomSheet;
        this.f$1 = list;
        this.f$2 = z;
        this.f$3 = z2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1050x3da8babf(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
