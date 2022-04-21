package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class IntroActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ IntroActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ IntroActivity$$ExternalSyntheticLambda5(IntroActivity introActivity, String str) {
        this.f$0 = introActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2289lambda$checkContinueText$4$orgtelegramuiIntroActivity(this.f$1, tLObject, tL_error);
    }
}
