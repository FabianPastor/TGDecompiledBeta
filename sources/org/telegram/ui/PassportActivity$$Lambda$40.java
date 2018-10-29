package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;

final /* synthetic */ class PassportActivity$$Lambda$40 implements RequestDelegate {
    private final PassportActivity arg$1;
    private final ErrorRunnable arg$2;
    private final boolean arg$3;
    private final TL_secureRequiredType arg$4;
    private final TL_secureRequiredType arg$5;
    private final boolean arg$6;
    private final ArrayList arg$7;
    private final Runnable arg$8;

    PassportActivity$$Lambda$40(PassportActivity passportActivity, ErrorRunnable errorRunnable, boolean z, TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, boolean z2, ArrayList arrayList, Runnable runnable) {
        this.arg$1 = passportActivity;
        this.arg$2 = errorRunnable;
        this.arg$3 = z;
        this.arg$4 = tL_secureRequiredType;
        this.arg$5 = tL_secureRequiredType2;
        this.arg$6 = z2;
        this.arg$7 = arrayList;
        this.arg$8 = runnable;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$deleteValueInternal$60$PassportActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, tLObject, tL_error);
    }
}
