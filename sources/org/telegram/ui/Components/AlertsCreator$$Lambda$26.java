package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class AlertsCreator$$Lambda$26 implements OnClickListener {
    private final int[] arg$1;
    private final boolean arg$2;
    private final BaseFragment arg$3;
    private final Runnable arg$4;

    AlertsCreator$$Lambda$26(int[] iArr, boolean z, BaseFragment baseFragment, Runnable runnable) {
        this.arg$1 = iArr;
        this.arg$2 = z;
        this.arg$3 = baseFragment;
        this.arg$4 = runnable;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createPopupSelectDialog$27$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, view);
    }
}
