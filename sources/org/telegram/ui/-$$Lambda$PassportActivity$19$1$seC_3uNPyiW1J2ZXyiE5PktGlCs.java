package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureValue;
import org.telegram.ui.PassportActivity.19.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$19$1$seC_3uNPyiW1J2ZXyiE5PktGlCs implements RequestDelegate {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ TL_secureValue f$1;

    public /* synthetic */ -$$Lambda$PassportActivity$19$1$seC_3uNPyiW1J2ZXyiE5PktGlCs(AnonymousClass1 anonymousClass1, TL_secureValue tL_secureValue) {
        this.f$0 = anonymousClass1;
        this.f$1 = tL_secureValue;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$run$4$PassportActivity$19$1(this.f$1, tLObject, tL_error);
    }
}
