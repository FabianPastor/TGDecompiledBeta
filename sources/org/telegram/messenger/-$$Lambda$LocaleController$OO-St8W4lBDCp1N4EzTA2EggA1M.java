package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocaleController$OO-St8W4lBDCp1N4EzTA2EggA1M implements RequestDelegate {
    private final /* synthetic */ LocaleController f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$LocaleController$OO-St8W4lBDCp1N4EzTA2EggA1M(LocaleController localeController, int i) {
        this.f$0 = localeController;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadRemoteLanguages$6$LocaleController(this.f$1, tLObject, tL_error);
    }
}
