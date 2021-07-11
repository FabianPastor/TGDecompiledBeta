package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$xKyuMKVyqNN_UNa_TeNOZCo1tYw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$xKyuMKVyqNN_UNa_TeNOZCo1tYw implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$xKyuMKVyqNN_UNa_TeNOZCo1tYw INSTANCE = new $$Lambda$ChatActivityEnterView$xKyuMKVyqNN_UNa_TeNOZCo1tYw();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$xKyuMKVyqNN_UNa_TeNOZCo1tYw() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
