package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.BooleanCallback;
import org.telegram.ui.DialogsActivity.AnonymousClass5;

final /* synthetic */ class DialogsActivity$5$$Lambda$3 implements BooleanCallback {
    private final AnonymousClass5 arg$1;
    private final long arg$2;
    private final int arg$3;
    private final boolean arg$4;
    private final boolean arg$5;

    DialogsActivity$5$$Lambda$3(AnonymousClass5 anonymousClass5, long j, int i, boolean z, boolean z2) {
        this.arg$1 = anonymousClass5;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = z;
        this.arg$5 = z2;
    }

    public void run(boolean z) {
        this.arg$1.lambda$null$5$DialogsActivity$5(this.arg$2, this.arg$3, this.arg$4, this.arg$5, z);
    }
}
