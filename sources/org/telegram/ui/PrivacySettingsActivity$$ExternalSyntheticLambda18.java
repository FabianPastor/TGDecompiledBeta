package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda18 implements RequestDelegate {
    public static final /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda18 INSTANCE = new PrivacySettingsActivity$$ExternalSyntheticLambda18();

    private /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda18() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PrivacySettingsActivity.lambda$createView$14(tLObject, tLRPC$TL_error);
    }
}
