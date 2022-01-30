package org.telegram.ui.Components;

import android.content.SharedPreferences;
import android.view.View;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda51 implements View.OnClickListener {
    public final /* synthetic */ int[] f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ SharedPreferences f$3;
    public final /* synthetic */ AlertDialog.Builder f$4;
    public final /* synthetic */ Runnable f$5;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda51(int[] iArr, long j, int i, SharedPreferences sharedPreferences, AlertDialog.Builder builder, Runnable runnable) {
        this.f$0 = iArr;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = sharedPreferences;
        this.f$4 = builder;
        this.f$5 = runnable;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createPrioritySelectDialog$84(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
    }
}
