package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class UndoView$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ UndoView f$0;
    public final /* synthetic */ TLObject f$1;

    public /* synthetic */ UndoView$$ExternalSyntheticLambda6(UndoView undoView, TLObject tLObject) {
        this.f$0 = undoView;
        this.f$1 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$showWithAction$4(this.f$1);
    }
}
