package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$b8qjtHw-SwhVCQIFBk7EnKFy_9c implements Runnable {
    private final /* synthetic */ PassportActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ ErrorRunnable f$2;
    private final /* synthetic */ boolean f$3;
    private final /* synthetic */ TL_secureRequiredType f$4;
    private final /* synthetic */ TL_secureRequiredType f$5;
    private final /* synthetic */ boolean f$6;
    private final /* synthetic */ ArrayList f$7;
    private final /* synthetic */ Runnable f$8;

    public /* synthetic */ -$$Lambda$PassportActivity$b8qjtHw-SwhVCQIFBk7EnKFy_9c(PassportActivity passportActivity, TL_error tL_error, ErrorRunnable errorRunnable, boolean z, TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, boolean z2, ArrayList arrayList, Runnable runnable) {
        this.f$0 = passportActivity;
        this.f$1 = tL_error;
        this.f$2 = errorRunnable;
        this.f$3 = z;
        this.f$4 = tL_secureRequiredType;
        this.f$5 = tL_secureRequiredType2;
        this.f$6 = z2;
        this.f$7 = arrayList;
        this.f$8 = runnable;
    }

    public final void run() {
        this.f$0.lambda$null$60$PassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
