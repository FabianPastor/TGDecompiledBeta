package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class BotWebViewSheet$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ BotWebViewSheet f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda8(BotWebViewSheet botWebViewSheet, int i, long j) {
        this.f$0 = botWebViewSheet;
        this.f$1 = i;
        this.f$2 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$requestWebView$8(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
