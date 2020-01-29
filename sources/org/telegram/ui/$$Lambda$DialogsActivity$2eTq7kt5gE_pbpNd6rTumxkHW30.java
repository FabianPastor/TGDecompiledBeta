package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;

/* renamed from: org.telegram.ui.-$$Lambda$DialogsActivity$2eTq7kt5gE_pbpNd6rTumxkHW30  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DialogsActivity$2eTq7kt5gE_pbpNd6rTumxkHW30 implements DialogInterface.OnClickListener {
    public static final /* synthetic */ $$Lambda$DialogsActivity$2eTq7kt5gE_pbpNd6rTumxkHW30 INSTANCE = new $$Lambda$DialogsActivity$2eTq7kt5gE_pbpNd6rTumxkHW30();

    private /* synthetic */ $$Lambda$DialogsActivity$2eTq7kt5gE_pbpNd6rTumxkHW30() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askedAboutMiuiLockscreen", true).commit();
    }
}
