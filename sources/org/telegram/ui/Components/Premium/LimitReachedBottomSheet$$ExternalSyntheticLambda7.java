package org.telegram.ui.Components.Premium;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LimitReachedBottomSheet$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ LimitReachedBottomSheet f$0;

    public /* synthetic */ LimitReachedBottomSheet$$ExternalSyntheticLambda7(LimitReachedBottomSheet limitReachedBottomSheet) {
        this.f$0 = limitReachedBottomSheet;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$revokeLinks$7(tLObject, tLRPC$TL_error);
    }
}
