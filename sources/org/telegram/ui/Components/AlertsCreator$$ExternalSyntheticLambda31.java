package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.ui.LanguageSelectActivity;
import org.telegram.ui.LaunchActivity;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda31 implements DialogInterface.OnClickListener {
    public final /* synthetic */ LaunchActivity f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda31(LaunchActivity launchActivity) {
        this.f$0 = launchActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$runLinkRequest$62(new LanguageSelectActivity());
    }
}
