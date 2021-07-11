package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$qbB8wUW2E4LuhJNciJXvdK-jcX0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$qbB8wUW2E4LuhJNciJXvdKjcX0 implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$qbB8wUW2E4LuhJNciJXvdKjcX0 INSTANCE = new $$Lambda$ChatActivityEnterView$qbB8wUW2E4LuhJNciJXvdKjcX0();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$qbB8wUW2E4LuhJNciJXvdKjcX0() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
