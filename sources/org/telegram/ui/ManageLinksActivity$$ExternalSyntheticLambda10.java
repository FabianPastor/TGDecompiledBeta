package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ManageLinksActivity$$ExternalSyntheticLambda10 implements RequestDelegate {
    public final /* synthetic */ ManageLinksActivity f$0;

    public /* synthetic */ ManageLinksActivity$$ExternalSyntheticLambda10(ManageLinksActivity manageLinksActivity) {
        this.f$0 = manageLinksActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$createView$7(tLObject, tLRPC$TL_error);
    }
}
