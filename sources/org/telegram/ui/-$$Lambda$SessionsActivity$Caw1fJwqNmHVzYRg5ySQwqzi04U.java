package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_authorization;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SessionsActivity$Caw1fJwqNmHVzYRg5ySQwqzi04U implements Runnable {
    private final /* synthetic */ SessionsActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TL_authorization f$3;

    public /* synthetic */ -$$Lambda$SessionsActivity$Caw1fJwqNmHVzYRg5ySQwqzi04U(SessionsActivity sessionsActivity, AlertDialog alertDialog, TL_error tL_error, TL_authorization tL_authorization) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
        this.f$3 = tL_authorization;
    }

    public final void run() {
        this.f$0.lambda$null$10$SessionsActivity(this.f$1, this.f$2, this.f$3);
    }
}
