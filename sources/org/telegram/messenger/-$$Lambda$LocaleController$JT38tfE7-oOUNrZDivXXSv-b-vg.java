package org.telegram.messenger;

import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LocaleController$JT38tfE7-oOUNrZDivXXSv-b-vg implements RequestDelegate {
    private final /* synthetic */ LocaleController f$0;
    private final /* synthetic */ LocaleInfo f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$LocaleController$JT38tfE7-oOUNrZDivXXSv-b-vg(LocaleController localeController, LocaleInfo localeInfo, int i) {
        this.f$0 = localeController;
        this.f$1 = localeInfo;
        this.f$2 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$applyRemoteLanguage$12$LocaleController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
