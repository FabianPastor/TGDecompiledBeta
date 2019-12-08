package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.messages_Messages;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$TTMiEnqYaYe2HHAdhUxlHMYi1Ik implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ messages_Messages f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ boolean f$6;

    public /* synthetic */ -$$Lambda$MessagesStorage$TTMiEnqYaYe2HHAdhUxlHMYi1Ik(MessagesStorage messagesStorage, boolean z, long j, messages_Messages messages_messages, int i, int i2, boolean z2) {
        this.f$0 = messagesStorage;
        this.f$1 = z;
        this.f$2 = j;
        this.f$3 = messages_messages;
        this.f$4 = i;
        this.f$5 = i2;
        this.f$6 = z2;
    }

    public final void run() {
        this.f$0.lambda$putMessages$138$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
