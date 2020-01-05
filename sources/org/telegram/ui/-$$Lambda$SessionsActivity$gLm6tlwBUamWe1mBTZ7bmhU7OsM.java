package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SessionsActivity$gLm6tlwBUamWe1mBTZ7bmhU7OsM implements RequestDelegate {
    private final /* synthetic */ SessionsActivity f$0;

    public /* synthetic */ -$$Lambda$SessionsActivity$gLm6tlwBUamWe1mBTZ7bmhU7OsM(SessionsActivity sessionsActivity) {
        this.f$0 = sessionsActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$loadSessions$17$SessionsActivity(tLObject, tL_error);
    }
}
