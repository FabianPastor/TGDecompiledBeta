package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class BotWebViewContainer$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ BotWebViewContainer f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ BotWebViewContainer$$ExternalSyntheticLambda7(BotWebViewContainer botWebViewContainer, String str) {
        this.f$0 = botWebViewContainer;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m580x7d6dddff(this.f$1, tLObject, tL_error);
    }
}
