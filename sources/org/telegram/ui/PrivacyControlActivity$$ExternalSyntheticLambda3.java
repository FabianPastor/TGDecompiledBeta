package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class PrivacyControlActivity$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ PrivacyControlActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ PrivacyControlActivity$$ExternalSyntheticLambda3(PrivacyControlActivity privacyControlActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = privacyControlActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m3004xb60e266c(this.f$1, this.f$2);
    }
}
