package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$syzpMtTwphGwWxsLrHqlm0H7ZL0 implements OnClickListener {
    private final /* synthetic */ int[] f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ Builder f$3;
    private final /* synthetic */ Runnable f$4;

    public /* synthetic */ -$$Lambda$AlertsCreator$syzpMtTwphGwWxsLrHqlm0H7ZL0(int[] iArr, long j, String str, Builder builder, Runnable runnable) {
        this.f$0 = iArr;
        this.f$1 = j;
        this.f$2 = str;
        this.f$3 = builder;
        this.f$4 = runnable;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createVibrationSelectDialog$36(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, view);
    }
}
