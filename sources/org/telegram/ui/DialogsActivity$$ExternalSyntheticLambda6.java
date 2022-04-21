package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda6 implements DialogInterface.OnClickListener {
    public static final /* synthetic */ DialogsActivity$$ExternalSyntheticLambda6 INSTANCE = new DialogsActivity$$ExternalSyntheticLambda6();

    private /* synthetic */ DialogsActivity$$ExternalSyntheticLambda6() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
