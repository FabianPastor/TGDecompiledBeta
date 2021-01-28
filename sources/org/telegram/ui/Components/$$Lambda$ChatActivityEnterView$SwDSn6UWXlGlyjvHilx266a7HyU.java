package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$SwDSn6UWXlGlyjvHilx266a7HyU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$SwDSn6UWXlGlyjvHilx266a7HyU implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$SwDSn6UWXlGlyjvHilx266a7HyU INSTANCE = new $$Lambda$ChatActivityEnterView$SwDSn6UWXlGlyjvHilx266a7HyU();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$SwDSn6UWXlGlyjvHilx266a7HyU() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
