package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda42 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda42 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda42();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda42() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
