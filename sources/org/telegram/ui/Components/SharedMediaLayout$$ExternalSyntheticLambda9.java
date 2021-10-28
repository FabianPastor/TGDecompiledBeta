package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class SharedMediaLayout$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ SharedMediaLayout f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ SharedMediaLayout$$ExternalSyntheticLambda9(SharedMediaLayout sharedMediaLayout, int i, TLObject tLObject) {
        this.f$0 = sharedMediaLayout;
        this.f$1 = i;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadFastScrollData$12(this.f$1, this.f$2);
    }
}
