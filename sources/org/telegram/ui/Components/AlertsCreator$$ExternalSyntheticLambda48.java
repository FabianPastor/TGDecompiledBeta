package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda48 implements View.OnClickListener {
    public final /* synthetic */ int[] f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ AlertDialog.Builder f$3;
    public final /* synthetic */ Runnable f$4;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda48(int[] iArr, long j, String str, AlertDialog.Builder builder, Runnable runnable) {
        this.f$0 = iArr;
        this.f$1 = j;
        this.f$2 = str;
        this.f$3 = builder;
        this.f$4 = runnable;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createVibrationSelectDialog$69(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, view);
    }
}
