package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda220 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ long[] f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda220(ChatActivity chatActivity, Runnable runnable, long[] jArr) {
        this.f$0 = chatActivity;
        this.f$1 = runnable;
        this.f$2 = jArr;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createMenu$164(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
