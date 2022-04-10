package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda4 implements DialogInterface.OnClickListener {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ Theme.ResourcesProvider f$2;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda4(long j, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = j;
        this.f$1 = baseFragment;
        this.f$2 = resourcesProvider;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createMuteAlert$83(this.f$0, this.f$1, this.f$2, dialogInterface, i);
    }
}
