package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda38 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda38 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda38();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda38() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
