package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda5 implements DialogInterface.OnClickListener {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ BaseFragment f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda5(long j, BaseFragment baseFragment) {
        this.f$0 = j;
        this.f$1 = baseFragment;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createMuteAlert$58(this.f$0, this.f$1, dialogInterface, i);
    }
}
