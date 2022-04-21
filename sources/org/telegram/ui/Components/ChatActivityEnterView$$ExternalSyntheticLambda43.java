package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda43 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda43 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda43();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda43() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
