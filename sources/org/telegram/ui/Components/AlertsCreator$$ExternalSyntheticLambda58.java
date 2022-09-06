package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda58 implements View.OnClickListener {
    public final /* synthetic */ int[] f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ AlertDialog.Builder f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda58(int[] iArr, int i, AlertDialog.Builder builder, Runnable runnable) {
        this.f$0 = iArr;
        this.f$1 = i;
        this.f$2 = builder;
        this.f$3 = runnable;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createPopupSelectDialog$111(this.f$0, this.f$1, this.f$2, this.f$3, view);
    }
}
