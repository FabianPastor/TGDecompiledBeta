package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$TooManyCommunitiesActivity$qV42ga1ks09mGejrAg9KDcO7hro implements RequestDelegate {
    private final /* synthetic */ TooManyCommunitiesActivity f$0;

    public /* synthetic */ -$$Lambda$TooManyCommunitiesActivity$qV42ga1ks09mGejrAg9KDcO7hro(TooManyCommunitiesActivity tooManyCommunitiesActivity) {
        this.f$0 = tooManyCommunitiesActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadInactiveChannels$5$TooManyCommunitiesActivity(tLObject, tL_error);
    }
}
