package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda211 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ Runnable f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda211(MessagesController messagesController, long j, int i, int i2, long j2, Runnable runnable) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = i2;
        this.f$4 = j2;
        this.f$5 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m186x9b6dab5a(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
