package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda15 implements DialogInterface.OnClickListener {
    public static final /* synthetic */ DialogsActivity$$ExternalSyntheticLambda15 INSTANCE = new DialogsActivity$$ExternalSyntheticLambda15();

    private /* synthetic */ DialogsActivity$$ExternalSyntheticLambda15() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
