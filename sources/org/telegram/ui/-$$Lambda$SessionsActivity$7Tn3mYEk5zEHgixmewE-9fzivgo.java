package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SessionsActivity$7Tn3mYEk5zEHgixmewE-9fzivgo implements RequestDelegate {
    private final /* synthetic */ SessionsActivity f$0;
    private final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ -$$Lambda$SessionsActivity$7Tn3mYEk5zEHgixmewE-9fzivgo(SessionsActivity sessionsActivity, AlertDialog alertDialog) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$2$SessionsActivity(this.f$1, tLObject, tL_error);
    }
}
