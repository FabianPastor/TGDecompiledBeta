package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda46 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda46 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda46();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda46() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
