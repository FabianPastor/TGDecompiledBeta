package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.Components.TrendingStickersLayout;

public final /* synthetic */ class TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ TrendingStickersLayout.TrendingStickersAdapter f$0;

    public /* synthetic */ TrendingStickersLayout$TrendingStickersAdapter$$ExternalSyntheticLambda3(TrendingStickersLayout.TrendingStickersAdapter trendingStickersAdapter) {
        this.f$0 = trendingStickersAdapter;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadMoreStickerSets$3(tLObject, tLRPC$TL_error);
    }
}
