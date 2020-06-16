package org.telegram.ui.Components;

import android.view.View;
import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg implements View.OnClickListener {
    public static final /* synthetic */ $$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg INSTANCE = new $$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg();

    private /* synthetic */ $$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg() {
    }

    public final void onClick(View view) {
        MediaController.getInstance().playNextMessage();
    }
}
