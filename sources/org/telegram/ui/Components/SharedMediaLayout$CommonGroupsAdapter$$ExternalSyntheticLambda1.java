package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ SharedMediaLayout.CommonGroupsAdapter f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda1(SharedMediaLayout.CommonGroupsAdapter commonGroupsAdapter, int i) {
        this.f$0 = commonGroupsAdapter;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4376xfa1b5b69(this.f$1, tLObject, tL_error);
    }
}
