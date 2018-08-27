package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;

final /* synthetic */ class LaunchActivity$$Lambda$46 implements DialogsActivityDelegate {
    private final LaunchActivity arg$1;
    private final String arg$2;
    private final int arg$3;
    private final TL_contacts_resolvedPeer arg$4;

    LaunchActivity$$Lambda$46(LaunchActivity launchActivity, String str, int i, TL_contacts_resolvedPeer tL_contacts_resolvedPeer) {
        this.arg$1 = launchActivity;
        this.arg$2 = str;
        this.arg$3 = i;
        this.arg$4 = tL_contacts_resolvedPeer;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$null$9$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, dialogsActivity, arrayList, charSequence, z);
    }
}
