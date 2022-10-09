package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda17 implements RequestDelegate {
    public static final /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda17 INSTANCE = new PrivacySettingsActivity$$ExternalSyntheticLambda17();

    private /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda17() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PrivacySettingsActivity.lambda$onFragmentDestroy$0(tLObject, tLRPC$TL_error);
    }
}
