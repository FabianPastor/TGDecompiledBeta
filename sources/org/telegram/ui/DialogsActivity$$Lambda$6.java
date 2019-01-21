package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesController;

final /* synthetic */ class DialogsActivity$$Lambda$6 implements OnClickListener {
    static final OnClickListener $instance = new DialogsActivity$$Lambda$6();

    private DialogsActivity$$Lambda$6() {
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
