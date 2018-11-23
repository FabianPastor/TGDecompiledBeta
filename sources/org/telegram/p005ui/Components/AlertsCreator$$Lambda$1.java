package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.LanguageSelectActivity;
import org.telegram.p005ui.LaunchActivity;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$1 */
final /* synthetic */ class AlertsCreator$$Lambda$1 implements OnClickListener {
    private final LaunchActivity arg$1;

    AlertsCreator$$Lambda$1(LaunchActivity launchActivity) {
        this.arg$1 = launchActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.presentFragment(new LanguageSelectActivity());
    }
}
