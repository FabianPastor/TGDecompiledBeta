package org.telegram.ui.Adapters;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class BaseLocationAdapter$$ExternalSyntheticLambda5 implements RequestDelegate {
    public final /* synthetic */ BaseLocationAdapter f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ BaseLocationAdapter$$ExternalSyntheticLambda5(BaseLocationAdapter baseLocationAdapter, String str) {
        this.f$0 = baseLocationAdapter;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchPlacesWithQuery$5(this.f$1, tLObject, tLRPC$TL_error);
    }
}
