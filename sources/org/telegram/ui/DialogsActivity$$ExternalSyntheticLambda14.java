package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda14 implements DialogInterface.OnClickListener {
    public static final /* synthetic */ DialogsActivity$$ExternalSyntheticLambda14 INSTANCE = new DialogsActivity$$ExternalSyntheticLambda14();

    private /* synthetic */ DialogsActivity$$ExternalSyntheticLambda14() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
