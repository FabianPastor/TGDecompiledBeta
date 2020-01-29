package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$cYQEOux9T0ign7UCS-0M-hNsaHk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$cYQEOux9T0ign7UCS0MhNsaHk implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$cYQEOux9T0ign7UCS0MhNsaHk INSTANCE = new $$Lambda$ChatActivityEnterView$cYQEOux9T0ign7UCS0MhNsaHk();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$cYQEOux9T0ign7UCS0MhNsaHk() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
