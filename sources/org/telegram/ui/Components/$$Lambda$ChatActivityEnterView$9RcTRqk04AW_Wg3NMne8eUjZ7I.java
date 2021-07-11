package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$9RcTRqk04-AW_Wg3NMne8eUjZ7I  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$9RcTRqk04AW_Wg3NMne8eUjZ7I implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$9RcTRqk04AW_Wg3NMne8eUjZ7I INSTANCE = new $$Lambda$ChatActivityEnterView$9RcTRqk04AW_Wg3NMne8eUjZ7I();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$9RcTRqk04AW_Wg3NMne8eUjZ7I() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
