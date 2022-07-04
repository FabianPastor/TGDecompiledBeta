package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SharedMediaLayout.CommonGroupsAdapter f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda0(SharedMediaLayout.CommonGroupsAdapter commonGroupsAdapter, TLRPC.TL_error tL_error, TLObject tLObject, int i) {
        this.f$0 = commonGroupsAdapter;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.m1392x8febd34a(this.f$1, this.f$2, this.f$3);
    }
}
