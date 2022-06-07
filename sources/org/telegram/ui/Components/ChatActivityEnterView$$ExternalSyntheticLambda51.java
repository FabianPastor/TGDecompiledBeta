package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda51 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda51 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda51();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda51() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
