package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class MessagesController$$Lambda$124 implements RequestDelegate {
    private final MessagesController arg$1;
    private final ArrayList arg$2;
    private final long arg$3;

    MessagesController$$Lambda$124(MessagesController messagesController, ArrayList arrayList, long j) {
        this.arg$1 = messagesController;
        this.arg$2 = arrayList;
        this.arg$3 = j;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadPinnedDialogs$203$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
