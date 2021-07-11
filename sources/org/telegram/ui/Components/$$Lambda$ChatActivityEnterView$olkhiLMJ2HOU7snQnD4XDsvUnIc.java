package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$olkhiLMJ2HOU7snQnD4XDsvUnIc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$olkhiLMJ2HOU7snQnD4XDsvUnIc implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$olkhiLMJ2HOU7snQnD4XDsvUnIc INSTANCE = new $$Lambda$ChatActivityEnterView$olkhiLMJ2HOU7snQnD4XDsvUnIc();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$olkhiLMJ2HOU7snQnD4XDsvUnIc() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
