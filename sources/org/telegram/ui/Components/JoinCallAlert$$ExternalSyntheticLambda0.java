package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.AccountInstance;

public final /* synthetic */ class JoinCallAlert$$ExternalSyntheticLambda0 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ AccountInstance f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ JoinCallAlert$$ExternalSyntheticLambda0(AccountInstance accountInstance, int i) {
        this.f$0 = accountInstance;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.getConnectionsManager().cancelRequest(this.f$1, true);
    }
}
