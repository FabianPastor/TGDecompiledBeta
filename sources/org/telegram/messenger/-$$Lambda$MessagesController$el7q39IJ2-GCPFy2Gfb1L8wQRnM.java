package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$el7q39IJ2-GCPFy2Gfb1L8wQRnM implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$MessagesController$el7q39IJ2-GCPFy2Gfb1L8wQRnM(MessagesController messagesController, int i) {
        this.f$0 = messagesController;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadPinnedDialogs$238$MessagesController(this.f$1, tLObject, tL_error);
    }
}