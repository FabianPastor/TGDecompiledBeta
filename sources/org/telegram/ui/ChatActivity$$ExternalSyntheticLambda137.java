package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda137 implements RequestDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ long[] f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda137(ChatActivity chatActivity, Runnable runnable, long[] jArr) {
        this.f$0 = chatActivity;
        this.f$1 = runnable;
        this.f$2 = jArr;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2923lambda$createMenu$167$orgtelegramuiChatActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
