package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessageStatisticActivity$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ MessageStatisticActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ MessageStatisticActivity$$ExternalSyntheticLambda2(MessageStatisticActivity messageStatisticActivity, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        this.f$0 = messageStatisticActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$loadStat$7(this.f$1, this.f$2);
    }
}
