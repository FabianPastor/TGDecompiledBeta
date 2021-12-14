package org.telegram.ui.Components;

import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class SlotsDrawable$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ SlotsDrawable f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ChatMessageCell f$2;

    public /* synthetic */ SlotsDrawable$$ExternalSyntheticLambda7(SlotsDrawable slotsDrawable, int i, ChatMessageCell chatMessageCell) {
        this.f$0 = slotsDrawable;
        this.f$1 = i;
        this.f$2 = chatMessageCell;
    }

    public final void run() {
        this.f$0.lambda$setBaseDice$4(this.f$1, this.f$2);
    }
}
