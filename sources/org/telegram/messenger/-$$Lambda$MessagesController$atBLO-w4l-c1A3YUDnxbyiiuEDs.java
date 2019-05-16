package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$atBLO-w4l-c1A3YUDnxbyiiuEDs implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ Integer f$3;

    public /* synthetic */ -$$Lambda$MessagesController$atBLO-w4l-c1A3YUDnxbyiiuEDs(MessagesController messagesController, TL_error tL_error, TLObject tLObject, Integer num) {
        this.f$0 = messagesController;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = num;
    }

    public final void run() {
        this.f$0.lambda$null$73$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
