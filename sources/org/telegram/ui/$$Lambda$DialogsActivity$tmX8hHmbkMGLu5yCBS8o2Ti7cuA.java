package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;

/* renamed from: org.telegram.ui.-$$Lambda$DialogsActivity$tmX8hHmbkMGLu5yCBS8o2Ti7cuA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DialogsActivity$tmX8hHmbkMGLu5yCBS8o2Ti7cuA implements DialogInterface.OnClickListener {
    public static final /* synthetic */ $$Lambda$DialogsActivity$tmX8hHmbkMGLu5yCBS8o2Ti7cuA INSTANCE = new $$Lambda$DialogsActivity$tmX8hHmbkMGLu5yCBS8o2Ti7cuA();

    private /* synthetic */ $$Lambda$DialogsActivity$tmX8hHmbkMGLu5yCBS8o2Ti7cuA() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
