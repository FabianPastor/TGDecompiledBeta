package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ReactedHeaderView$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ ReactedHeaderView f$0;

    public /* synthetic */ ReactedHeaderView$$ExternalSyntheticLambda4(ReactedHeaderView reactedHeaderView) {
        this.f$0 = reactedHeaderView;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadReactions$7(tLObject, tLRPC$TL_error);
    }
}
