package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda21 implements DialogInterface.OnClickListener {
    public final /* synthetic */ BaseFragment f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda21(BaseFragment baseFragment) {
        this.f$0 = baseFragment;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.f$0.getCurrentAccount()).openByUserName("spambot", this.f$0, 1);
    }
}
