package org.telegram.p005ui;

import android.content.Context;
import org.telegram.p005ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.DialogsActivity$$Lambda$0 */
final /* synthetic */ class DialogsActivity$$Lambda$0 implements Runnable {
    private final Context arg$1;

    DialogsActivity$$Lambda$0(Context context) {
        this.arg$1 = context;
    }

    public void run() {
        Theme.createChatResources(this.arg$1, false);
    }
}
