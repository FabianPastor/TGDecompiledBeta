package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ SharedMediaLayout.CommonGroupsAdapter f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ SharedMediaLayout$CommonGroupsAdapter$$ExternalSyntheticLambda1(SharedMediaLayout.CommonGroupsAdapter commonGroupsAdapter, int i) {
        this.f$0 = commonGroupsAdapter;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$getChats$1(this.f$1, tLObject, tLRPC$TL_error);
    }
}
