package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda2 implements DialogInterface.OnClickListener {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ Context f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda2(int i, BaseFragment baseFragment, Context context, long j) {
        this.f$0 = i;
        this.f$1 = baseFragment;
        this.f$2 = context;
        this.f$3 = j;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createReportAlert$61(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}
