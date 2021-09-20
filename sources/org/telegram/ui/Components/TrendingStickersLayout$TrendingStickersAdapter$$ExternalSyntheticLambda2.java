package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.TrendingStickersLayout;

public final /* synthetic */ class TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ TrendingStickersLayout.TrendingStickersAdapter f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda2(TrendingStickersLayout.TrendingStickersAdapter trendingStickersAdapter, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.f$0 = trendingStickersAdapter;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadMoreStickerSets$2(this.f$1, this.f$2);
    }
}
