package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ Context f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ Theme.ResourcesProvider f$4;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda1(int i, BaseFragment baseFragment, Context context, long j, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = i;
        this.f$1 = baseFragment;
        this.f$2 = context;
        this.f$3 = j;
        this.f$4 = resourcesProvider;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createReportAlert$64(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
    }
}
