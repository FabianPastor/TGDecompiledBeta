package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class CommonGroupsActivity$$Lambda$3 implements Runnable {
    private final CommonGroupsActivity arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final int arg$4;

    CommonGroupsActivity$$Lambda$3(CommonGroupsActivity commonGroupsActivity, TL_error tL_error, TLObject tLObject, int i) {
        this.arg$1 = commonGroupsActivity;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$null$1$CommonGroupsActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
