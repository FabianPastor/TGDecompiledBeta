package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$3yJK8SQxnnH2lwzsz-XvhwGap8Q  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$3yJK8SQxnnH2lwzszXvhwGap8Q implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$3yJK8SQxnnH2lwzszXvhwGap8Q INSTANCE = new $$Lambda$ChatActivityEnterView$3yJK8SQxnnH2lwzszXvhwGap8Q();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$3yJK8SQxnnH2lwzszXvhwGap8Q() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
