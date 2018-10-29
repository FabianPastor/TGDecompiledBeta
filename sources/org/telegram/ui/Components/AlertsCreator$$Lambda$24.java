package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.LaunchActivity;

final /* synthetic */ class AlertsCreator$$Lambda$24 implements OnClickListener {
    private final LaunchActivity arg$1;

    AlertsCreator$$Lambda$24(LaunchActivity launchActivity) {
        this.arg$1 = launchActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.presentFragment(new CacheControlActivity());
    }
}
