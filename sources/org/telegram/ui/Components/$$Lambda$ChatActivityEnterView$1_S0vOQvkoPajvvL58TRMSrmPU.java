package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$1_S0vOQv-koPajvvL58TRMSrmPU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$1_S0vOQvkoPajvvL58TRMSrmPU implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$1_S0vOQvkoPajvvL58TRMSrmPU INSTANCE = new $$Lambda$ChatActivityEnterView$1_S0vOQvkoPajvvL58TRMSrmPU();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$1_S0vOQvkoPajvvL58TRMSrmPU() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
