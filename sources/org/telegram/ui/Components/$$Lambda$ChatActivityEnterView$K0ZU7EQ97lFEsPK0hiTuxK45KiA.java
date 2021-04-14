package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$K0ZU7EQ97lFEsPK0hiTuxK45KiA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$K0ZU7EQ97lFEsPK0hiTuxK45KiA implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$K0ZU7EQ97lFEsPK0hiTuxK45KiA INSTANCE = new $$Lambda$ChatActivityEnterView$K0ZU7EQ97lFEsPK0hiTuxK45KiA();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$K0ZU7EQ97lFEsPK0hiTuxK45KiA() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
