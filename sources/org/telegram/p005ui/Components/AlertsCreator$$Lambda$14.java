package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesController;
import org.telegram.p005ui.ActionBar.BaseFragment;

/* renamed from: org.telegram.ui.Components.AlertsCreator$$Lambda$14 */
final /* synthetic */ class AlertsCreator$$Lambda$14 implements OnClickListener {
    private final BaseFragment arg$1;

    AlertsCreator$$Lambda$14(BaseFragment baseFragment) {
        this.arg$1 = baseFragment;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.arg$1.getCurrentAccount()).openByUserName("spambot", this.arg$1, 1);
    }
}
