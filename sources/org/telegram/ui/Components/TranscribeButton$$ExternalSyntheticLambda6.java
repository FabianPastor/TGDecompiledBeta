package org.telegram.ui.Components;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class TranscribeButton$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ MessageObject f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ int f$4;

    public /* synthetic */ TranscribeButton$$ExternalSyntheticLambda6(MessageObject messageObject, long j, int i, long j2, int i2) {
        this.f$0 = messageObject;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = j2;
        this.f$4 = i2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        TranscribeButton.lambda$transcribePressed$4(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
