package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class AlertsCreator$$Lambda$25 implements OnClickListener {
    private final int[] arg$1;
    private final long arg$2;
    private final boolean arg$3;
    private final BaseFragment arg$4;
    private final Runnable arg$5;

    AlertsCreator$$Lambda$25(int[] iArr, long j, boolean z, BaseFragment baseFragment, Runnable runnable) {
        this.arg$1 = iArr;
        this.arg$2 = j;
        this.arg$3 = z;
        this.arg$4 = baseFragment;
        this.arg$5 = runnable;
    }

    public void onClick(View view) {
        AlertsCreator.lambda$createPrioritySelectDialog$26$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5, view);
    }
}
