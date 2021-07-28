package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$2qMbG7RhYFjf5DGZry30sFnt1D8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$2qMbG7RhYFjf5DGZry30sFnt1D8 implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$2qMbG7RhYFjf5DGZry30sFnt1D8 INSTANCE = new $$Lambda$ChatActivityEnterView$2qMbG7RhYFjf5DGZry30sFnt1D8();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$2qMbG7RhYFjf5DGZry30sFnt1D8() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
