package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda7 implements RequestDelegate {
    public static final /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda7 INSTANCE = new PremiumPreviewFragment$$ExternalSyntheticLambda7();

    private /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda7() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PremiumPreviewFragment.lambda$sentPremiumButtonClick$8(tLObject, tLRPC$TL_error);
    }
}
