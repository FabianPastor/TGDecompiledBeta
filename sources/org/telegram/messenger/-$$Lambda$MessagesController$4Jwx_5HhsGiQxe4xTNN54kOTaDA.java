package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$4Jwx_5HhsGiQxe4xTNN54kOTaDA implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Dialog f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MessagesController$4Jwx_5HhsGiQxe4xTNN54kOTaDA(MessagesController messagesController, int i, Dialog dialog, long j) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = dialog;
        this.f$3 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$checkLastDialogMessage$152$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}