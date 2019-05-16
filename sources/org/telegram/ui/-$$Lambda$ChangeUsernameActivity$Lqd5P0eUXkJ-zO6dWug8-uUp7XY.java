package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangeUsernameActivity$Lqd5P0eUXkJ-zO6dWug8-uUp7XY implements Runnable {
    private final /* synthetic */ ChangeUsernameActivity f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TLObject f$3;

    public /* synthetic */ -$$Lambda$ChangeUsernameActivity$Lqd5P0eUXkJ-zO6dWug8-uUp7XY(ChangeUsernameActivity changeUsernameActivity, String str, TL_error tL_error, TLObject tLObject) {
        this.f$0 = changeUsernameActivity;
        this.f$1 = str;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$2$ChangeUsernameActivity(this.f$1, this.f$2, this.f$3);
    }
}
