package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class AlertsCreator$$Lambda$11 implements OnClickListener {
    private final long arg$1;
    private final int arg$2;
    private final BaseFragment arg$3;
    private final Context arg$4;

    AlertsCreator$$Lambda$11(long j, int i, BaseFragment baseFragment, Context context) {
        this.arg$1 = j;
        this.arg$2 = i;
        this.arg$3 = baseFragment;
        this.arg$4 = context;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createReportAlert$12$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, dialogInterface, i);
    }
}
