package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.TrendingStickersLayout;

public final /* synthetic */ class TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ TrendingStickersLayout.TrendingStickersAdapter f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda2(TrendingStickersLayout.TrendingStickersAdapter trendingStickersAdapter, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = trendingStickersAdapter;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m2699x4da32db6(this.f$1, this.f$2);
    }
}
