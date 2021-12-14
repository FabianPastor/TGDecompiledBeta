package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class IntroActivity$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ IntroActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ IntroActivity$$ExternalSyntheticLambda3(IntroActivity introActivity, String str) {
        this.f$0 = introActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkContinueText$3(this.f$1, tLObject, tLRPC$TL_error);
    }
}
