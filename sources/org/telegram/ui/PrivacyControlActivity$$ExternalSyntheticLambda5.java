package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PrivacyControlActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ PrivacyControlActivity f$0;

    public /* synthetic */ PrivacyControlActivity$$ExternalSyntheticLambda5(PrivacyControlActivity privacyControlActivity) {
        this.f$0 = privacyControlActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$applyCurrentPrivacySettings$4(tLObject, tLRPC$TL_error);
    }
}
