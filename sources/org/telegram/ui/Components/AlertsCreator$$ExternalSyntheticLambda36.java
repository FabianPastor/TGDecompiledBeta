package org.telegram.ui.Components;

import android.content.DialogInterface;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda36 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ Runnable f$0;
    public final /* synthetic */ boolean[] f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda36(Runnable runnable, boolean[] zArr) {
        this.f$0 = runnable;
        this.f$1 = zArr;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        AlertsCreator.lambda$createScheduleDatePickerDialog$47(this.f$0, this.f$1, dialogInterface);
    }
}
