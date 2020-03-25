package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$DbhNVr7aGBPkzi2ZSveTFbR_EbU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$DbhNVr7aGBPkzi2ZSveTFbR_EbU implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$DbhNVr7aGBPkzi2ZSveTFbR_EbU INSTANCE = new $$Lambda$ChatActivityEnterView$DbhNVr7aGBPkzi2ZSveTFbR_EbU();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$DbhNVr7aGBPkzi2ZSveTFbR_EbU() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
