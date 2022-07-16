package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda11 implements RequestDelegate {
    public static final /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda11 INSTANCE = new PremiumPreviewFragment$$ExternalSyntheticLambda11();

    private /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda11() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PremiumPreviewFragment.lambda$sentPremiumBuyCanceled$14(tLObject, tLRPC$TL_error);
    }
}
