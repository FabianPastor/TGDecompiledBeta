package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda42 implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda42 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda42();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda42() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
