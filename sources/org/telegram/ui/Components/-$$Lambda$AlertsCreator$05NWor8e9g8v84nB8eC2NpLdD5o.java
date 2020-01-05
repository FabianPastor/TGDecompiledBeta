package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$05NWor8e9g8v84nB8eC2NpLdD5o implements OnClickListener {
    private final /* synthetic */ BaseFragment f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$05NWor8e9g8v84nB8eC2NpLdD5o(BaseFragment baseFragment) {
        this.f$0 = baseFragment;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.f$0.getCurrentAccount()).openByUserName("spambot", this.f$0, 1);
    }
}
