package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;

final /* synthetic */ class PassportActivity$$Lambda$54 implements Runnable {
    private final PassportActivity arg$1;
    private final TL_error arg$2;
    private final ErrorRunnable arg$3;
    private final boolean arg$4;
    private final TL_secureRequiredType arg$5;
    private final TL_secureRequiredType arg$6;
    private final boolean arg$7;
    private final ArrayList arg$8;
    private final Runnable arg$9;

    PassportActivity$$Lambda$54(PassportActivity passportActivity, TL_error tL_error, ErrorRunnable errorRunnable, boolean z, TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, boolean z2, ArrayList arrayList, Runnable runnable) {
        this.arg$1 = passportActivity;
        this.arg$2 = tL_error;
        this.arg$3 = errorRunnable;
        this.arg$4 = z;
        this.arg$5 = tL_secureRequiredType;
        this.arg$6 = tL_secureRequiredType2;
        this.arg$7 = z2;
        this.arg$8 = arrayList;
        this.arg$9 = runnable;
    }

    public void run() {
        this.arg$1.lambda$null$59$PassportActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9);
    }
}
