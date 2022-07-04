package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda50 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda50 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda50();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda50() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
