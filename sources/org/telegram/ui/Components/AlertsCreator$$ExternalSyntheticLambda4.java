package org.telegram.ui.Components;

import android.content.DialogInterface;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda4 implements DialogInterface.OnClickListener {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda4(long j, Runnable runnable) {
        this.f$0 = j;
        this.f$1 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createColorSelectDialog$71(this.f$0, this.f$1, dialogInterface, i);
    }
}
