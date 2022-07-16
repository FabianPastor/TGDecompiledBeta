package org.telegram.ui.Cells;

import org.telegram.tgnet.TLRPC$TL_premiumGiftOption;

public final /* synthetic */ class ChatActionCell$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ChatActionCell f$0;
    public final /* synthetic */ TLRPC$TL_premiumGiftOption f$1;

    public /* synthetic */ ChatActionCell$$ExternalSyntheticLambda1(ChatActionCell chatActionCell, TLRPC$TL_premiumGiftOption tLRPC$TL_premiumGiftOption) {
        this.f$0 = chatActionCell;
        this.f$1 = tLRPC$TL_premiumGiftOption;
    }

    public final void run() {
        this.f$0.lambda$openPremiumGiftPreview$1(this.f$1);
    }
}
