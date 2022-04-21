package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$26$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ DialogsActivity.AnonymousClass26 f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.Dialog f$2;

    public /* synthetic */ DialogsActivity$26$$ExternalSyntheticLambda0(DialogsActivity.AnonymousClass26 r1, int i, TLRPC.Dialog dialog) {
        this.f$0 = r1;
        this.f$1 = i;
        this.f$2 = dialog;
    }

    public final void run() {
        this.f$0.m2119lambda$onRemoveDialogAction$0$orgtelegramuiDialogsActivity$26(this.f$1, this.f$2);
    }
}
