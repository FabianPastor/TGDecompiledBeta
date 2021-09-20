package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LinkEditActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ LinkEditActivity f$0;

    public /* synthetic */ LinkEditActivity$$ExternalSyntheticLambda7(LinkEditActivity linkEditActivity) {
        this.f$0 = linkEditActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$7(tLObject, tLRPC$TL_error);
    }
}
