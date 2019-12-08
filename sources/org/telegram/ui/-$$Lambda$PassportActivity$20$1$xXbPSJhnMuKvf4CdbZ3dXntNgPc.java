package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;
import org.telegram.ui.PassportActivity.20.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$20$1$xXbPSJhnMuKvf4CdbZ3dXntNgPc implements RequestDelegate {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ TL_secureRequiredType f$2;
    private final /* synthetic */ PassportActivityDelegate f$3;
    private final /* synthetic */ ErrorRunnable f$4;

    public /* synthetic */ -$$Lambda$PassportActivity$20$1$xXbPSJhnMuKvf4CdbZ3dXntNgPc(AnonymousClass1 anonymousClass1, String str, TL_secureRequiredType tL_secureRequiredType, PassportActivityDelegate passportActivityDelegate, ErrorRunnable errorRunnable) {
        this.f$0 = anonymousClass1;
        this.f$1 = str;
        this.f$2 = tL_secureRequiredType;
        this.f$3 = passportActivityDelegate;
        this.f$4 = errorRunnable;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$run$2$PassportActivity$20$1(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
