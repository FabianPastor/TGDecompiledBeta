package org.telegram.ui.Components;

import android.content.DialogInterface;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda9 implements DialogInterface.OnClickListener {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda9(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.run();
    }
}
