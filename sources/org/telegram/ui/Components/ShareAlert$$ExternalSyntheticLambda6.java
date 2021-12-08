package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ShareAlert$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ ShareAlert f$0;
    public final /* synthetic */ Context f$1;

    public /* synthetic */ ShareAlert$$ExternalSyntheticLambda6(ShareAlert shareAlert, Context context) {
        this.f$0 = shareAlert;
        this.f$1 = context;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$new$1(this.f$1, tLObject, tLRPC$TL_error);
    }
}
