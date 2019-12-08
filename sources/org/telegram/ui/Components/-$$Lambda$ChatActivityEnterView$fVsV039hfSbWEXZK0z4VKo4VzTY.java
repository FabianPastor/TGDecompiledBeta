package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$fVsV039hfSbWEXZK0z4VKo4VzTY implements ScheduleDatePickerDelegate {
    public static final /* synthetic */ -$$Lambda$ChatActivityEnterView$fVsV039hfSbWEXZK0z4VKo4VzTY INSTANCE = new -$$Lambda$ChatActivityEnterView$fVsV039hfSbWEXZK0z4VKo4VzTY();

    private /* synthetic */ -$$Lambda$ChatActivityEnterView$fVsV039hfSbWEXZK0z4VKo4VzTY() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
