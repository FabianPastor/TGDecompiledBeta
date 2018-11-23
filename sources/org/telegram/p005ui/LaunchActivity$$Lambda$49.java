package org.telegram.p005ui;

import java.util.ArrayList;
import org.telegram.p005ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$49 */
final /* synthetic */ class LaunchActivity$$Lambda$49 implements DialogsActivityDelegate {
    private final LaunchActivity arg$1;
    private final int arg$2;
    private final User arg$3;
    private final String arg$4;

    LaunchActivity$$Lambda$49(LaunchActivity launchActivity, int i, User user, String str) {
        this.arg$1 = launchActivity;
        this.arg$2 = i;
        this.arg$3 = user;
        this.arg$4 = str;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$null$10$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, dialogsActivity, arrayList, charSequence, z);
    }
}
