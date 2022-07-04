package org.telegram.ui.Components;

import org.telegram.ui.Cells.ChatMessageCell;

public final /* synthetic */ class SlotsDrawable$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ SlotsDrawable f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ ChatMessageCell f$3;

    public /* synthetic */ SlotsDrawable$$ExternalSyntheticLambda10(SlotsDrawable slotsDrawable, boolean z, int i, ChatMessageCell chatMessageCell) {
        this.f$0 = slotsDrawable;
        this.f$1 = z;
        this.f$2 = i;
        this.f$3 = chatMessageCell;
    }

    public final void run() {
        this.f$0.lambda$setDiceNumber$9(this.f$1, this.f$2, this.f$3);
    }
}
