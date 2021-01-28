package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$44g6c3aAW7sDflaKG9ziTOh46AE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$44g6c3aAW7sDflaKG9ziTOh46AE implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$44g6c3aAW7sDflaKG9ziTOh46AE INSTANCE = new $$Lambda$ChatActivityEnterView$44g6c3aAW7sDflaKG9ziTOh46AE();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$44g6c3aAW7sDflaKG9ziTOh46AE() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
