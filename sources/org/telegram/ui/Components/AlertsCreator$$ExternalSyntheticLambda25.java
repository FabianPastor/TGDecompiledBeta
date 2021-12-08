package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.LaunchActivity;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda25 implements DialogInterface.OnClickListener {
    public final /* synthetic */ LaunchActivity f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda25(LaunchActivity launchActivity) {
        this.f$0 = launchActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3109lambda$runLinkRequest$43$orgtelegramuiLaunchActivity(new CacheControlActivity());
    }
}
