package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class BaseLocationAdapter$$ExternalSyntheticLambda4 implements RequestDelegate {
    public final /* synthetic */ BaseLocationAdapter f$0;

    public /* synthetic */ BaseLocationAdapter$$ExternalSyntheticLambda4(BaseLocationAdapter baseLocationAdapter) {
        this.f$0 = baseLocationAdapter;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchBotUser$3(tLObject, tLRPC$TL_error);
    }
}
