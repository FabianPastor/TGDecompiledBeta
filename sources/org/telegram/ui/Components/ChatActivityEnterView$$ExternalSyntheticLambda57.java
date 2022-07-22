package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda57 implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda57 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda57();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda57() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
