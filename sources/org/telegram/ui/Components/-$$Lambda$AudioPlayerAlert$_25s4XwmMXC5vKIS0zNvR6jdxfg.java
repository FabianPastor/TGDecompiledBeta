package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.messenger.MediaController;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg implements OnClickListener {
    public static final /* synthetic */ -$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg INSTANCE = new -$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg();

    private /* synthetic */ -$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg() {
    }

    public final void onClick(View view) {
        MediaController.getInstance().playPreviousMessage();
    }
}
