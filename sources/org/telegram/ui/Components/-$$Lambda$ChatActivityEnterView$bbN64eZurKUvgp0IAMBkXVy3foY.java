package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$bbN64eZurKUvgp0IAMBkXVy3foY implements ScheduleDatePickerDelegate {
    public static final /* synthetic */ -$$Lambda$ChatActivityEnterView$bbN64eZurKUvgp0IAMBkXVy3foY INSTANCE = new -$$Lambda$ChatActivityEnterView$bbN64eZurKUvgp0IAMBkXVy3foY();

    private /* synthetic */ -$$Lambda$ChatActivityEnterView$bbN64eZurKUvgp0IAMBkXVy3foY() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
