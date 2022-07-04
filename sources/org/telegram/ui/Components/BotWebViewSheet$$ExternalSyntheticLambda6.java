package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class BotWebViewSheet$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ BotWebViewSheet f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda6(BotWebViewSheet botWebViewSheet, int i) {
        this.f$0 = botWebViewSheet;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m629xb1b45c8e(this.f$1, tLObject, tL_error);
    }
}
