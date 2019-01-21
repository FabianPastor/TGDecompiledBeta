package org.telegram.ui;

import android.content.Context;
import org.telegram.ui.ActionBar.Theme;

final /* synthetic */ class DialogsActivity$$Lambda$0 implements Runnable {
    private final Context arg$1;

    DialogsActivity$$Lambda$0(Context context) {
        this.arg$1 = context;
    }

    public void run() {
        Theme.createChatResources(this.arg$1, false);
    }
}
