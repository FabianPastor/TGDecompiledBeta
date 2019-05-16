package org.telegram.ui;

import android.content.Context;
import org.telegram.ui.ActionBar.Theme;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogsActivity$Wz8eSYJLAR-xfNaDl4aQWUwW9N0 implements Runnable {
    private final /* synthetic */ Context f$0;

    public /* synthetic */ -$$Lambda$DialogsActivity$Wz8eSYJLAR-xfNaDl4aQWUwW9N0(Context context) {
        this.f$0 = context;
    }

    public final void run() {
        Theme.createChatResources(this.f$0, false);
    }
}
