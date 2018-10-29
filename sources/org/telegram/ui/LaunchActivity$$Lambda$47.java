package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;

final /* synthetic */ class LaunchActivity$$Lambda$47 implements DialogsActivityDelegate {
    private final LaunchActivity arg$1;
    private final int arg$2;
    private final User arg$3;
    private final String arg$4;

    LaunchActivity$$Lambda$47(LaunchActivity launchActivity, int i, User user, String str) {
        this.arg$1 = launchActivity;
        this.arg$2 = i;
        this.arg$3 = user;
        this.arg$4 = str;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$null$10$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, dialogsActivity, arrayList, charSequence, z);
    }
}
