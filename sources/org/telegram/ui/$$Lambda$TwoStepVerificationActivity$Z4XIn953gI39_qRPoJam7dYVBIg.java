package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.ui.-$$Lambda$TwoStepVerificationActivity$Z4XIn953gI39_qRPoJam7dYVBIg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TwoStepVerificationActivity$Z4XIn953gI39_qRPoJam7dYVBIg implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$TwoStepVerificationActivity$Z4XIn953gI39_qRPoJam7dYVBIg INSTANCE = new $$Lambda$TwoStepVerificationActivity$Z4XIn953gI39_qRPoJam7dYVBIg();

    private /* synthetic */ $$Lambda$TwoStepVerificationActivity$Z4XIn953gI39_qRPoJam7dYVBIg() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TwoStepVerificationActivity.lambda$checkSecretValues$17(tLObject, tLRPC$TL_error);
    }
}
