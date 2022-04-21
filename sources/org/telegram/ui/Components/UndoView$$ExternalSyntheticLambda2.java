package org.telegram.ui.Components;

import android.view.View;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class UndoView$$ExternalSyntheticLambda2 implements View.OnClickListener {
    public final /* synthetic */ UndoView f$0;
    public final /* synthetic */ TLRPC.Message f$1;

    public /* synthetic */ UndoView$$ExternalSyntheticLambda2(UndoView undoView, TLRPC.Message message) {
        this.f$0 = undoView;
        this.f$1 = message;
    }

    public final void onClick(View view) {
        this.f$0.m4508lambda$showWithAction$6$orgtelegramuiComponentsUndoView(this.f$1, view);
    }
}
