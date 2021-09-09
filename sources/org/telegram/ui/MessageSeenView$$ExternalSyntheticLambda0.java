package org.telegram.ui;

import org.telegram.tgnet.TLObject;

public final /* synthetic */ class MessageSeenView$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ MessageSeenView f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MessageSeenView$$ExternalSyntheticLambda0(MessageSeenView messageSeenView, TLObject tLObject, int i) {
        this.f$0 = messageSeenView;
        this.f$1 = tLObject;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$new$0(this.f$1, this.f$2);
    }
}
