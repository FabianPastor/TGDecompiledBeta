package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessageStatisticActivity$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ MessageStatisticActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ MessageStatisticActivity$$ExternalSyntheticLambda2(MessageStatisticActivity messageStatisticActivity, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = messageStatisticActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m3952lambda$loadChats$2$orgtelegramuiMessageStatisticActivity(this.f$1, this.f$2);
    }
}
