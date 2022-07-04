package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.ui.LanguageSelectActivity;
import org.telegram.ui.LaunchActivity;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda53 implements DialogInterface.OnClickListener {
    public final /* synthetic */ LaunchActivity f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda53(LaunchActivity launchActivity) {
        this.f$0 = launchActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new LanguageSelectActivity());
    }
}
