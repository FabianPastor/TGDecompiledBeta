package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$SharedMediaPreloader$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SharedMediaLayout.SharedMediaPreloader f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ SharedMediaLayout$SharedMediaPreloader$$ExternalSyntheticLambda0(SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader, TLObject tLObject) {
        this.f$0 = sharedMediaPreloader;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$0(this.f$1);
    }
}
