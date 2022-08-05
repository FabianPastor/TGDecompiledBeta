package org.telegram.messenger;

import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda76 implements Runnable {
    public final /* synthetic */ BaseFragment f$0;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda76(BaseFragment baseFragment) {
        this.f$0 = baseFragment;
    }

    public final void run() {
        AlertsCreator.showSendMediaAlert(8, this.f$0, (Theme.ResourcesProvider) null);
    }
}
