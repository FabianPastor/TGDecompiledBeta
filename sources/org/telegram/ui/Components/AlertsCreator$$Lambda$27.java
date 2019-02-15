package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.LaunchActivity;

final /* synthetic */ class AlertsCreator$$Lambda$27 implements OnClickListener {
    private final LaunchActivity arg$1;

    AlertsCreator$$Lambda$27(LaunchActivity launchActivity) {
        this.arg$1 = launchActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$runLinkRequest$27$LaunchActivity(new CacheControlActivity());
    }
}
