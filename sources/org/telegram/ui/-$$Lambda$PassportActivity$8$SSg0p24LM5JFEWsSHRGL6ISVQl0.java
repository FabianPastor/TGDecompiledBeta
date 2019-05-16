package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.AnonymousClass8;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$8$SSg0p24LM5JFEWsSHRGL6ISVQl0 implements Runnable {
    private final /* synthetic */ AnonymousClass8 f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$PassportActivity$8$SSg0p24LM5JFEWsSHRGL6ISVQl0(AnonymousClass8 anonymousClass8, TL_error tL_error, TLObject tLObject) {
        this.f$0 = anonymousClass8;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$0$PassportActivity$8(this.f$1, this.f$2);
    }
}
