package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.CacheControlActivity;
import org.telegram.p005ui.LaunchActivity;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$26 */
final /* synthetic */ class AlertsCreator$$Lambda$26 implements OnClickListener {
    private final LaunchActivity arg$1;

    AlertsCreator$$Lambda$26(LaunchActivity launchActivity) {
        this.arg$1 = launchActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.presentFragment(new CacheControlActivity());
    }
}
