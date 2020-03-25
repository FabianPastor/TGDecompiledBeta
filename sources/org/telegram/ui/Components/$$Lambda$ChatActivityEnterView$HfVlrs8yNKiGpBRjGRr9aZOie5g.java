package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$HfVlrs8yNKiGpBRjGRr9aZOie5g  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$HfVlrs8yNKiGpBRjGRr9aZOie5g implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$HfVlrs8yNKiGpBRjGRr9aZOie5g INSTANCE = new $$Lambda$ChatActivityEnterView$HfVlrs8yNKiGpBRjGRr9aZOie5g();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$HfVlrs8yNKiGpBRjGRr9aZOie5g() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
