package org.telegram.ui.Components;

import android.content.DialogInterface;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda5 implements DialogInterface.OnClickListener {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ int[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda5(long j, int[] iArr, int i, Runnable runnable) {
        this.f$0 = j;
        this.f$1 = iArr;
        this.f$2 = i;
        this.f$3 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createColorSelectDialog$90(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}
