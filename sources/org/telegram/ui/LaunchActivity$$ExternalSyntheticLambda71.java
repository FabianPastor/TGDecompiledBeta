package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda71 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda71(LaunchActivity launchActivity, int i) {
        this.f$0 = launchActivity;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkAppUpdate$77(this.f$1, tLObject, tLRPC$TL_error);
    }
}
