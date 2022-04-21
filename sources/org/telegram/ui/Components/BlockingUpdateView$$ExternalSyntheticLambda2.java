package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class BlockingUpdateView$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ BlockingUpdateView f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ BlockingUpdateView$$ExternalSyntheticLambda2(BlockingUpdateView blockingUpdateView, TLObject tLObject) {
        this.f$0 = blockingUpdateView;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.m3628lambda$show$2$orgtelegramuiComponentsBlockingUpdateView(this.f$1);
    }
}
