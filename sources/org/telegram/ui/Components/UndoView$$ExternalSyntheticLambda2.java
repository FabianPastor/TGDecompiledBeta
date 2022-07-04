package org.telegram.ui.Components;

import android.view.View;
import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class UndoView$$ExternalSyntheticLambda2 implements View.OnClickListener {
    public final /* synthetic */ UndoView f$0;
    public final /* synthetic */ TLRPC$Message f$1;

    public /* synthetic */ UndoView$$ExternalSyntheticLambda2(UndoView undoView, TLRPC$Message tLRPC$Message) {
        this.f$0 = undoView;
        this.f$1 = tLRPC$Message;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showWithAction$6(this.f$1, view);
    }
}
