package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$ntnzswOfvdfLiJpE9RVLQ3qgvgc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$ntnzswOfvdfLiJpE9RVLQ3qgvgc implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$ntnzswOfvdfLiJpE9RVLQ3qgvgc INSTANCE = new $$Lambda$ChatActivityEnterView$ntnzswOfvdfLiJpE9RVLQ3qgvgc();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$ntnzswOfvdfLiJpE9RVLQ3qgvgc() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
