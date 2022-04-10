package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda45 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda45 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda45();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda45() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
