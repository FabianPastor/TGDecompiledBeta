package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$9goMHps4vcUnXcLVLUkEn9GeTGE implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Dialog f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MessagesController$9goMHps4vcUnXcLVLUkEn9GeTGE(MessagesController messagesController, int i, Dialog dialog, long j) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = dialog;
        this.f$3 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$checkLastDialogMessage$151$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
