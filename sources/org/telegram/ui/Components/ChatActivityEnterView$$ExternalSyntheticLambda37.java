package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda37 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda37 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda37();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda37() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
