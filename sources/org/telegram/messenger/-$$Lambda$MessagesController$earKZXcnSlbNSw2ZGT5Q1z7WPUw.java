package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$earKZXcnSlbNSw2ZGT5Q1z7WPUw implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ long f$1;

    public /* synthetic */ -$$Lambda$MessagesController$earKZXcnSlbNSw2ZGT5Q1z7WPUw(MessagesController messagesController, long j) {
        this.f$0 = messagesController;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadPeerSettings$27$MessagesController(this.f$1, tLObject, tL_error);
    }
}