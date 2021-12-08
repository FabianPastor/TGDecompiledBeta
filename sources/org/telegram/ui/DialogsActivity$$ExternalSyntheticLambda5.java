package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda5 implements DialogInterface.OnClickListener {
    public static final /* synthetic */ DialogsActivity$$ExternalSyntheticLambda5 INSTANCE = new DialogsActivity$$ExternalSyntheticLambda5();

    private /* synthetic */ DialogsActivity$$ExternalSyntheticLambda5() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
