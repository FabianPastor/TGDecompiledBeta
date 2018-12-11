package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesController;

/* renamed from: org.telegram.ui.DialogsActivity$$Lambda$7 */
final /* synthetic */ class DialogsActivity$$Lambda$7 implements OnClickListener {
    static final OnClickListener $instance = new DialogsActivity$$Lambda$7();

    private DialogsActivity$$Lambda$7() {
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
