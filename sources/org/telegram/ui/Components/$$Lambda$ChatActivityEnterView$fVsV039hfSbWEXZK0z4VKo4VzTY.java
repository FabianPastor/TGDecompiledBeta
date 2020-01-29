package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$fVsV039hfSbWEXZK0z4VKo4VzTY  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$fVsV039hfSbWEXZK0z4VKo4VzTY implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$fVsV039hfSbWEXZK0z4VKo4VzTY INSTANCE = new $$Lambda$ChatActivityEnterView$fVsV039hfSbWEXZK0z4VKo4VzTY();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$fVsV039hfSbWEXZK0z4VKo4VzTY() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
