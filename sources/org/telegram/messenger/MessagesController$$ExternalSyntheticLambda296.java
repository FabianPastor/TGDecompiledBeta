package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda296 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ TLRPC$InputPeer f$6;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda296(MessagesController messagesController, long j, long j2, int i, int i2, boolean z, TLRPC$InputPeer tLRPC$InputPeer) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = j2;
        this.f$3 = i;
        this.f$4 = i2;
        this.f$5 = z;
        this.f$6 = tLRPC$InputPeer;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$deleteDialog$115(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
    }
}
