package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;

/* renamed from: org.telegram.ui.-$$Lambda$DialogsActivity$gBjuaG54WzRhYTxEGEJ-3AtBCLASSNAME  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DialogsActivity$gBjuaG54WzRhYTxEGEJ3AtBCLASSNAME implements DialogInterface.OnClickListener {
    public static final /* synthetic */ $$Lambda$DialogsActivity$gBjuaG54WzRhYTxEGEJ3AtBCLASSNAME INSTANCE = new $$Lambda$DialogsActivity$gBjuaG54WzRhYTxEGEJ3AtBCLASSNAME();

    private /* synthetic */ $$Lambda$DialogsActivity$gBjuaG54WzRhYTxEGEJ3AtBCLASSNAME() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
