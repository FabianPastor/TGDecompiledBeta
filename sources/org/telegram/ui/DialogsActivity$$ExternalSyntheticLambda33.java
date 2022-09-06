package org.telegram.ui;

import android.content.Context;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda33 implements Runnable {
    public final /* synthetic */ Context f$0;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda33(Context context) {
        this.f$0 = context;
    }

    public final void run() {
        Theme.createChatResources(this.f$0, false);
    }
}
