package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$lJhTu2kL_oJWhIxXlKFzIjK4Pt4 implements OnClickListener {
    private final /* synthetic */ long f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Runnable f$2;

    public /* synthetic */ -$$Lambda$AlertsCreator$lJhTu2kL_oJWhIxXlKFzIjK4Pt4(long j, int i, Runnable runnable) {
        this.f$0 = j;
        this.f$1 = i;
        this.f$2 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createColorSelectDialog$24(this.f$0, this.f$1, this.f$2, dialogInterface, i);
    }
}
