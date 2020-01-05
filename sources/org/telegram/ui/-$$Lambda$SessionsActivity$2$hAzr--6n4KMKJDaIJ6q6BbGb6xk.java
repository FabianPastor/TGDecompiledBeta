package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.SessionsActivity.AnonymousClass2;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SessionsActivity$2$hAzr--6n4KMKJDaIJ6q6BbGb6xk implements Runnable {
    private final /* synthetic */ AnonymousClass2 f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TL_authorization f$2;

    public /* synthetic */ -$$Lambda$SessionsActivity$2$hAzr--6n4KMKJDaIJ6q6BbGb6xk(AnonymousClass2 anonymousClass2, TL_error tL_error, TL_authorization tL_authorization) {
        this.f$0 = anonymousClass2;
        this.f$1 = tL_error;
        this.f$2 = tL_authorization;
    }

    public final void run() {
        this.f$0.lambda$null$0$SessionsActivity$2(this.f$1, this.f$2);
    }
}
