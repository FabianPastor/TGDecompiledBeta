package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda36 implements DialogInterface.OnClickListener {
    public final /* synthetic */ int[] f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ Context f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ Theme.ResourcesProvider f$5;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda36(int[] iArr, int i, BaseFragment baseFragment, Context context, long j, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = iArr;
        this.f$1 = i;
        this.f$2 = baseFragment;
        this.f$3 = context;
        this.f$4 = j;
        this.f$5 = resourcesProvider;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createReportAlert$87(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
    }
}
