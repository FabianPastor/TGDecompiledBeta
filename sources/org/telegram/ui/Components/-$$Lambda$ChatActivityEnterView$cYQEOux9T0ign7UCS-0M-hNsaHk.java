package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$cYQEOux9T0ign7UCS-0M-hNsaHk implements Runnable {
    public static final /* synthetic */ -$$Lambda$ChatActivityEnterView$cYQEOux9T0ign7UCS-0M-hNsaHk INSTANCE = new -$$Lambda$ChatActivityEnterView$cYQEOux9T0ign7UCS-0M-hNsaHk();

    private /* synthetic */ -$$Lambda$ChatActivityEnterView$cYQEOux9T0ign7UCS-0M-hNsaHk() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
