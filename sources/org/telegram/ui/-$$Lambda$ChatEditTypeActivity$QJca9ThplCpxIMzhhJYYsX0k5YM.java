package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatEditTypeActivity$QJca9ThplCpxIMzhhJYYsX0k5YM implements Runnable {
    private final /* synthetic */ ChatEditTypeActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$ChatEditTypeActivity$QJca9ThplCpxIMzhhJYYsX0k5YM(ChatEditTypeActivity chatEditTypeActivity, TL_error tL_error, TLObject tLObject, boolean z) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$null$20$ChatEditTypeActivity(this.f$1, this.f$2, this.f$3);
    }
}
