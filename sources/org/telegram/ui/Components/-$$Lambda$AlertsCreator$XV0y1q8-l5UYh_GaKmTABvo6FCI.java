package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$XV0y1q8-l5UYh_GaKmTABvo6FCI implements OnDismissListener {
    private final /* synthetic */ Runnable f$0;
    private final /* synthetic */ boolean[] f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$XV0y1q8-l5UYh_GaKmTABvo6FCI(Runnable runnable, boolean[] zArr) {
        this.f$0 = runnable;
        this.f$1 = zArr;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        AlertsCreator.lambda$createScheduleDatePickerDialog$27(this.f$0, this.f$1, dialogInterface);
    }
}
