package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessageStatisticActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ MessageStatisticActivity f$0;

    public /* synthetic */ MessageStatisticActivity$$ExternalSyntheticLambda5(MessageStatisticActivity messageStatisticActivity) {
        this.f$0 = messageStatisticActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadChats$3(tLObject, tLRPC$TL_error);
    }
}
