package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$jHmW4dMMcdkxHZAbFNR_a01rFNM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$jHmW4dMMcdkxHZAbFNR_a01rFNM implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$jHmW4dMMcdkxHZAbFNR_a01rFNM INSTANCE = new $$Lambda$ChatActivityEnterView$jHmW4dMMcdkxHZAbFNR_a01rFNM();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$jHmW4dMMcdkxHZAbFNR_a01rFNM() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
