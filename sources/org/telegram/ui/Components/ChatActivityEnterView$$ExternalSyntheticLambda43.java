package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda43 implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda43 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda43();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda43() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
