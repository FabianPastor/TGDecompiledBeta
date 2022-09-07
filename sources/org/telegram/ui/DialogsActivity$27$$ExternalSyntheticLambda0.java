package org.telegram.ui;

import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$27$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ DialogsActivity.AnonymousClass27 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$Dialog f$2;

    public /* synthetic */ DialogsActivity$27$$ExternalSyntheticLambda0(DialogsActivity.AnonymousClass27 r1, int i, TLRPC$Dialog tLRPC$Dialog) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = tLRPC$Dialog;
    }

    public final void run() {
        this.f$0.lambda$onRemoveDialogAction$0(this.f$1, this.f$2);
    }
}
