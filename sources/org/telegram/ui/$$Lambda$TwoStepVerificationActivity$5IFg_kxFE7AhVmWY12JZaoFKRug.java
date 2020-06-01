package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.ui.-$$Lambda$TwoStepVerificationActivity$5IFg_kxFE7AhVmWY12JZaoFKRug  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TwoStepVerificationActivity$5IFg_kxFE7AhVmWY12JZaoFKRug implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$TwoStepVerificationActivity$5IFg_kxFE7AhVmWY12JZaoFKRug INSTANCE = new $$Lambda$TwoStepVerificationActivity$5IFg_kxFE7AhVmWY12JZaoFKRug();

    private /* synthetic */ $$Lambda$TwoStepVerificationActivity$5IFg_kxFE7AhVmWY12JZaoFKRug() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TwoStepVerificationActivity.lambda$checkSecretValues$17(tLObject, tLRPC$TL_error);
    }
}
