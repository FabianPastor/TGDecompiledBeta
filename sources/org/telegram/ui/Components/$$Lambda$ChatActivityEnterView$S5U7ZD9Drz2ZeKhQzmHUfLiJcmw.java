package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$S5U7ZD9Drz2ZeKhQzmHUfLiJcmw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$S5U7ZD9Drz2ZeKhQzmHUfLiJcmw implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$S5U7ZD9Drz2ZeKhQzmHUfLiJcmw INSTANCE = new $$Lambda$ChatActivityEnterView$S5U7ZD9Drz2ZeKhQzmHUfLiJcmw();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$S5U7ZD9Drz2ZeKhQzmHUfLiJcmw() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
