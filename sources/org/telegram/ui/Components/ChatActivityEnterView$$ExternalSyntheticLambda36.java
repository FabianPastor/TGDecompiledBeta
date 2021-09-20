package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda36 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda36 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda36();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda36() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
