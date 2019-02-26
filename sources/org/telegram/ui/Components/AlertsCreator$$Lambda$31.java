package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

final /* synthetic */ class AlertsCreator$$Lambda$31 implements OnClickListener {
    private final int[] arg$1;
    private final int arg$2;
    private final Builder arg$3;
    private final Runnable arg$4;

    AlertsCreator$$Lambda$31(int[] iArr, int i, Builder builder, Runnable runnable) {
        this.arg$1 = iArr;
        this.arg$2 = i;
        this.arg$3 = builder;
        this.arg$4 = runnable;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createPopupSelectDialog$34$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, view);
    }
}
