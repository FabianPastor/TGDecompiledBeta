package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda51 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC$Message f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLRPC$Message f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ int f$6;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda51(SendMessagesHelper sendMessagesHelper, TLRPC$Message tLRPC$Message, long j, int i, TLRPC$Message tLRPC$Message2, int i2, int i3) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLRPC$Message;
        this.f$2 = j;
        this.f$3 = i;
        this.f$4 = tLRPC$Message2;
        this.f$5 = i2;
        this.f$6 = i3;
    }

    public final void run() {
        this.f$0.lambda$sendMessage$10(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
