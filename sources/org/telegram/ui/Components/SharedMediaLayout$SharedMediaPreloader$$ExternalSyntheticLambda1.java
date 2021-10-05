package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$SharedMediaPreloader$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ SharedMediaLayout.SharedMediaPreloader f$0;

    public /* synthetic */ SharedMediaLayout$SharedMediaPreloader$$ExternalSyntheticLambda1(SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader) {
        this.f$0 = sharedMediaPreloader;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$didReceivedNotification$1(tLObject, tLRPC$TL_error);
    }
}
