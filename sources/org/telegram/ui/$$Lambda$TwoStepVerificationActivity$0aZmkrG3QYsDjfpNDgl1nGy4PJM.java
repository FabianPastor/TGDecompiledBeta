package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.ui.-$$Lambda$TwoStepVerificationActivity$0aZmkrG3QYsDjfpNDgl1nGy4PJM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$TwoStepVerificationActivity$0aZmkrG3QYsDjfpNDgl1nGy4PJM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$TwoStepVerificationActivity$0aZmkrG3QYsDjfpNDgl1nGy4PJM INSTANCE = new $$Lambda$TwoStepVerificationActivity$0aZmkrG3QYsDjfpNDgl1nGy4PJM();

    private /* synthetic */ $$Lambda$TwoStepVerificationActivity$0aZmkrG3QYsDjfpNDgl1nGy4PJM() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        TwoStepVerificationActivity.lambda$checkSecretValues$26(tLObject, tL_error);
    }
}
