package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$7QmoU3-B6R1Qi7NRoCLASSNAMEpSu2cQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$7QmoU3B6R1Qi7NRoCLASSNAMEpSu2cQ implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$7QmoU3B6R1Qi7NRoCLASSNAMEpSu2cQ INSTANCE = new $$Lambda$ChatActivityEnterView$7QmoU3B6R1Qi7NRoCLASSNAMEpSu2cQ();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$7QmoU3B6R1Qi7NRoCLASSNAMEpSu2cQ() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
