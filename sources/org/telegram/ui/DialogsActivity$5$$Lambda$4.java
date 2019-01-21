package org.telegram.ui;

import org.telegram.ui.DialogsActivity.AnonymousClass5;

final /* synthetic */ class DialogsActivity$5$$Lambda$4 implements Runnable {
    private final AnonymousClass5 arg$1;
    private final int arg$2;
    private final boolean arg$3;
    private final long arg$4;
    private final boolean arg$5;

    DialogsActivity$5$$Lambda$4(AnonymousClass5 anonymousClass5, int i, boolean z, long j, boolean z2) {
        this.arg$1 = anonymousClass5;
        this.arg$2 = i;
        this.arg$3 = z;
        this.arg$4 = j;
        this.arg$5 = z2;
    }

    public void run() {
        this.arg$1.lambda$null$4$DialogsActivity$5(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
