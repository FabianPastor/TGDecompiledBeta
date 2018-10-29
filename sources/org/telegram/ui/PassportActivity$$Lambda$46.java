package org.telegram.ui;

import java.util.ArrayList;

final /* synthetic */ class PassportActivity$$Lambda$46 implements Runnable {
    private final PassportActivity arg$1;
    private final ArrayList arg$2;
    private final int arg$3;
    private final boolean arg$4;

    PassportActivity$$Lambda$46(PassportActivity passportActivity, ArrayList arrayList, int i, boolean z) {
        this.arg$1 = passportActivity;
        this.arg$2 = arrayList;
        this.arg$3 = i;
        this.arg$4 = z;
    }

    public void run() {
        this.arg$1.lambda$processSelectedFiles$72$PassportActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
