package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;
import org.telegram.ui.PassportActivity.20.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$20$1$RLCHh_-MgQ1hoS4C2TlelWtsOYg implements Runnable {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ TL_secureRequiredType f$3;
    private final /* synthetic */ PassportActivityDelegate f$4;
    private final /* synthetic */ TL_error f$5;
    private final /* synthetic */ ErrorRunnable f$6;

    public /* synthetic */ -$$Lambda$PassportActivity$20$1$RLCHh_-MgQ1hoS4C2TlelWtsOYg(AnonymousClass1 anonymousClass1, TLObject tLObject, String str, TL_secureRequiredType tL_secureRequiredType, PassportActivityDelegate passportActivityDelegate, TL_error tL_error, ErrorRunnable errorRunnable) {
        this.f$0 = anonymousClass1;
        this.f$1 = tLObject;
        this.f$2 = str;
        this.f$3 = tL_secureRequiredType;
        this.f$4 = passportActivityDelegate;
        this.f$5 = tL_error;
        this.f$6 = errorRunnable;
    }

    public final void run() {
        this.f$0.lambda$null$1$PassportActivity$20$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
