package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getMessagesViews;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$zcPVttRNdPhRMvGP9lvTiB9rsK8 implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TL_messages_getMessagesViews f$2;

    public /* synthetic */ -$$Lambda$MessagesController$zcPVttRNdPhRMvGP9lvTiB9rsK8(MessagesController messagesController, int i, TL_messages_getMessagesViews tL_messages_getMessagesViews) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tL_messages_getMessagesViews;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$updateTimerProc$80$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
