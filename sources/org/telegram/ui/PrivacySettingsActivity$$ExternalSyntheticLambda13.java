package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda13 implements RequestDelegate {
    public final /* synthetic */ PrivacySettingsActivity f$0;

    public /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda13(PrivacySettingsActivity privacySettingsActivity) {
        this.f$0 = privacySettingsActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadPasswordSettings$19(tLObject, tLRPC$TL_error);
    }
}
