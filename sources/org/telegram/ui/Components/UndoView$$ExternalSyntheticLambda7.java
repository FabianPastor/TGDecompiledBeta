package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class UndoView$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ UndoView f$0;

    public /* synthetic */ UndoView$$ExternalSyntheticLambda7(UndoView undoView) {
        this.f$0 = undoView;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$showWithAction$5(tLObject, tLRPC$TL_error);
    }
}
