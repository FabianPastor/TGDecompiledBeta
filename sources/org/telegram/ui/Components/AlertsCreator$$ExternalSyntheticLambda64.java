package org.telegram.ui.Components;

import android.content.DialogInterface;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda64 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda64(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        AlertsCreator.lambda$createDeleteMessagesAlert$119(this.f$0, dialogInterface);
    }
}
