package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;

/* renamed from: org.telegram.ui.-$$Lambda$DialogsActivity$ilxOfJA0tIm_rfnhWJ6WLKeQIIo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DialogsActivity$ilxOfJA0tIm_rfnhWJ6WLKeQIIo implements DialogInterface.OnClickListener {
    public static final /* synthetic */ $$Lambda$DialogsActivity$ilxOfJA0tIm_rfnhWJ6WLKeQIIo INSTANCE = new $$Lambda$DialogsActivity$ilxOfJA0tIm_rfnhWJ6WLKeQIIo();

    private /* synthetic */ $$Lambda$DialogsActivity$ilxOfJA0tIm_rfnhWJ6WLKeQIIo() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
