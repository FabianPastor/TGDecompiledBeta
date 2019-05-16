package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$IntroActivity$v08WUA33DIz9dwdLeC-Z0gtPf-4 implements RequestDelegate {
    private final /* synthetic */ IntroActivity f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$IntroActivity$v08WUA33DIz9dwdLeC-Z0gtPf-4(IntroActivity introActivity, String str) {
        this.f$0 = introActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$checkContinueText$4$IntroActivity(this.f$1, tLObject, tL_error);
    }
}
