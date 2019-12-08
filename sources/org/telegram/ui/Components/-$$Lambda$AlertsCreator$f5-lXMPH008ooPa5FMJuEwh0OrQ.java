package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$f5-lXMPH008ooPa5FMJuEwh0OrQ implements OnClickListener {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ int[] f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ Runnable f$3;

    public /* synthetic */ -$$Lambda$AlertsCreator$f5-lXMPH008ooPa5FMJuEwh0OrQ(long j, int[] iArr, int i, Runnable runnable) {
        this.f$0 = j;
        this.f$1 = iArr;
        this.f$2 = i;
        this.f$3 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createColorSelectDialog$35(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}
