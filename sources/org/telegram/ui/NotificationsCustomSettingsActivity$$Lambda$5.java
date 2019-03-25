package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;

final /* synthetic */ class NotificationsCustomSettingsActivity$$Lambda$5 implements DialogsActivityDelegate {
    private final NotificationsCustomSettingsActivity arg$1;

    NotificationsCustomSettingsActivity$$Lambda$5(NotificationsCustomSettingsActivity notificationsCustomSettingsActivity) {
        this.arg$1 = notificationsCustomSettingsActivity;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$null$2$NotificationsCustomSettingsActivity(dialogsActivity, arrayList, charSequence, z);
    }
}
