package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesController;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogsActivity$om5eIuKoD-TUjWJtmdVNYsc-Woc implements OnClickListener {
    public static final /* synthetic */ -$$Lambda$DialogsActivity$om5eIuKoD-TUjWJtmdVNYsc-Woc INSTANCE = new -$$Lambda$DialogsActivity$om5eIuKoD-TUjWJtmdVNYsc-Woc();

    private /* synthetic */ -$$Lambda$DialogsActivity$om5eIuKoD-TUjWJtmdVNYsc-Woc() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
