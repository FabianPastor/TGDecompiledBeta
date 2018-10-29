package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;

final /* synthetic */ class LaunchActivity$$Lambda$11 implements DialogsActivityDelegate {
    private final LaunchActivity arg$1;
    private final boolean arg$2;
    private final int arg$3;
    private final String arg$4;

    LaunchActivity$$Lambda$11(LaunchActivity launchActivity, boolean z, int i, String str) {
        this.arg$1 = launchActivity;
        this.arg$2 = z;
        this.arg$3 = i;
        this.arg$4 = str;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$runLinkRequest$18$LaunchActivity(this.arg$2, this.arg$3, this.arg$4, dialogsActivity, arrayList, charSequence, z);
    }
}
